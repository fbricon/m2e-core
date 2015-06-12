/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *      Red Hat, Inc. - initial API and implementation
 *******************************************************************************/

package org.eclipse.m2e.core.ui.internal.search.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * Artifact search engine based on calls to the SOLR API on
 * <a href="http://search.maven.org">http://search.maven.org</a>
 *
 * @author Fred Bricon
 */
public class MavenCentralSearchEngine implements SearchEngine {

  private OkHttpClient client = new OkHttpClient();

  private String baseUrl = "http://search.maven.org/solrsearch/select?";

  private String params = "wt=json&q=";

  private static final Logger log = LoggerFactory.getLogger(MavenCentralSearchEngine.class);

  private static Cache<String, Collection<String>> queryCache = CacheBuilder.newBuilder().maximumSize(50)
      .expireAfterAccess(5, TimeUnit.MINUTES).build();

  /* (non-Javadoc)
   * @see org.eclipse.m2e.core.ui.internal.search.util.SearchEngine#findGroupIds(java.lang.String, org.eclipse.m2e.core.ui.internal.search.util.Packaging, org.eclipse.m2e.core.ui.internal.search.util.ArtifactInfo)
   */
  @SuppressWarnings("synthetic-access")
  public Collection<String> findGroupIds(final String searchExpression, Packaging packaging,
      ArtifactInfo containingArtifact) {
    String query = "g:" + searchExpression.replace("\\", "") + "* ";
    final StringBuilder url = new StringBuilder();
    try {
      //XXX the solr search returns duplicate groupIds, as it returns groupid/artifactId combos, which is not interesting
      url.append(baseUrl).append("rows=200&").append(params).append(URLEncoder.encode(query, "UTF-8"));
    } catch(UnsupportedEncodingException ex1) {
      log.error(ex1.getMessage(), ex1);
    }
    Collection<String> result = null;
    try {
      result = queryCache.get(url.toString(), new Callable<Collection<String>>() {
        public Collection<String> call() throws Exception {
          Request request = new Request.Builder().url(url.toString()).build();
          Response response = client.newCall(request).execute();
          if(response.isSuccessful()) {
            JsonObject json = new JsonParser().parse(response.body().charStream()).getAsJsonObject();
            JsonArray hits = json.get("response").getAsJsonObject().get("docs").getAsJsonArray();
            List<String> groupIds = new ArrayList<>(hits.size());
            for(JsonElement hit : hits) {
              String groupId = hit.getAsJsonObject().get("g").getAsString();
              if(!groupIds.contains(groupId)) {
                groupIds.add(groupId);
              }
            }
            Collections.sort(groupIds);
            return groupIds;
          }
          return Collections.emptyList();
        }
      });
    } catch(ExecutionException ex) {
      log.error(ex.getMessage(), ex);
      result = Collections.emptyList();
    }
    return result;
  }

  /* (non-Javadoc)
   * @see org.eclipse.m2e.core.ui.internal.search.util.SearchEngine#findArtifactIds(java.lang.String, java.lang.String, org.eclipse.m2e.core.ui.internal.search.util.Packaging, org.eclipse.m2e.core.ui.internal.search.util.ArtifactInfo)
   */
  public Collection<String> findArtifactIds(String groupId, String searchExpression, Packaging packaging,
      ArtifactInfo containingArtifact) {
    String query = "";
    boolean hasGroup = false;

    if(groupId != null && !groupId.isEmpty()) {
      query += "g:\"" + groupId + "\"";
      hasGroup = true;
    }
    if(searchExpression != null && !searchExpression.isEmpty()) {
      query += ((hasGroup) ? " AND " : "") + "a:" + searchExpression + "*";
    }
    String url = "";
    try {
      url = baseUrl + "rows=100&" + params + URLEncoder.encode(query, "UTF-8");
    } catch(UnsupportedEncodingException ex1) {
      log.error(ex1.getMessage(), ex1);
    }

    Request request = new Request.Builder().url(url).build();
    try {
      Response response = client.newCall(request).execute();
      if(response.isSuccessful()) {
        JsonObject json = new JsonParser().parse(response.body().charStream()).getAsJsonObject();
        JsonArray hits = json.get("response").getAsJsonObject().get("docs").getAsJsonArray();
        List<String> artifactIds = new ArrayList<>(hits.size());
        for(JsonElement hit : hits) {
          String artifactId = hit.getAsJsonObject().get("a").getAsString();
          artifactIds.add(artifactId);
        }
        return artifactIds;
      }
    } catch(Exception ex) {
      log.error(ex.getMessage(), ex);
    }
    return Collections.emptyList();
  }

  /* (non-Javadoc)
   * @see org.eclipse.m2e.core.ui.internal.search.util.SearchEngine#findVersions(java.lang.String, java.lang.String, java.lang.String, org.eclipse.m2e.core.ui.internal.search.util.Packaging)
   */
  public Collection<String> findVersions(String groupId, String artifactId, String searchExpression,
      Packaging packaging) {
    String query = "g:\"" + groupId + "\" AND a:\"" + artifactId + "\"";
    if(searchExpression != null && !searchExpression.isEmpty()) {
      query += " AND v:" + searchExpression + "*";
    }
    String url = "";
    try {
      url = baseUrl + "rows=50&" + "core=gav&" + params + URLEncoder.encode(query, "UTF-8");
    } catch(UnsupportedEncodingException ex1) {
      log.error(ex1.getMessage(), ex1);
    }

    Request request = new Request.Builder().url(url).build();
    try {
      Response response = client.newCall(request).execute();
      if(response.isSuccessful()) {
        JsonObject json = new JsonParser().parse(response.body().charStream()).getAsJsonObject();
        JsonArray hits = json.get("response").getAsJsonObject().get("docs").getAsJsonArray();
        List<String> versions = new ArrayList<>(hits.size());
        for(JsonElement hit : hits) {
          String version = hit.getAsJsonObject().get("v").getAsString();
          versions.add(version);
        }
        return versions;
      }
    } catch(Exception ex) {
      log.error("Unable to read " + url + " : " + ex.getMessage());
    }
    return Collections.emptyList();
  }

  /* (non-Javadoc)
   * @see org.eclipse.m2e.core.ui.internal.search.util.SearchEngine#findClassifiers(java.lang.String, java.lang.String, java.lang.String, java.lang.String, org.eclipse.m2e.core.ui.internal.search.util.Packaging)
   */
  public Collection<String> findClassifiers(String groupId, String artifactId, String version, String prefix,
      Packaging packaging) {
    String query = "g:\"" + groupId + "\" AND a:\"" + artifactId + "\" AND v:\"" + version + "\"";
    if(prefix != null && !prefix.isEmpty()) {
      query += " AND l:" + prefix + "*";
    }
    String url = "";
    try {
      url = baseUrl + "rows=50&" + params + URLEncoder.encode(query, "UTF-8");
    } catch(UnsupportedEncodingException ex1) {
      log.error(ex1.getMessage(), ex1);
    }

    Request request = new Request.Builder().url(url).build();
    try {
      Response response = client.newCall(request).execute();
      if(response.isSuccessful()) {
        JsonObject json = new JsonParser().parse(response.body().charStream()).getAsJsonObject();
        JsonArray hits = json.get("response").getAsJsonObject().get("docs").getAsJsonArray();
        List<String> classifiers = new ArrayList<>(hits.size());
        for(JsonElement hit : hits) {
          JsonArray ec = hit.getAsJsonObject().get("ec").getAsJsonArray();
          for(JsonElement cl : ec) {
            String fileType = cl.getAsString();
            if(fileType.startsWith("-")) {
              String classifier;
              if(fileType.lastIndexOf(".") > 0) {
                classifier = fileType.substring(1, fileType.lastIndexOf("."));
              } else {
                classifier = fileType.substring(1);
              }
              if(classifier.startsWith(prefix)) {
                classifiers.add(classifier);
              }
            }
          }
        }
        return classifiers;
      }
    } catch(Exception ex) {
      log.error(ex.getMessage(), ex);
    }
    return Collections.emptyList();
  }

  /* (non-Javadoc)
   * @see org.eclipse.m2e.core.ui.internal.search.util.SearchEngine#findTypes(java.lang.String, java.lang.String, java.lang.String, java.lang.String, org.eclipse.m2e.core.ui.internal.search.util.Packaging)
   */
  public Collection<String> findTypes(String groupId, String artifactId, String version, String prefix,
      Packaging packaging) {
    String query = "g:\"" + groupId + "\" AND a:\"" + artifactId + "\" AND v:\"" + version + "\" AND p:" + prefix + "*";
    String url = "";
    try {
      url = baseUrl + "rows=50&" + params + URLEncoder.encode(query, "UTF-8");
    } catch(UnsupportedEncodingException ex1) {
      log.error(ex1.getMessage(), ex1);
    }

    Request request = new Request.Builder().url(url).build();
    try {
      Response response = client.newCall(request).execute();
      if(response.isSuccessful()) {
        JsonObject json = new JsonParser().parse(response.body().charStream()).getAsJsonObject();
        JsonArray hits = json.get("response").getAsJsonObject().get("docs").getAsJsonArray();
        List<String> types = new ArrayList<>(hits.size());
        for(JsonElement hit : hits) {
          String type = hit.getAsJsonObject().get("p").getAsString();
          types.add(type);
        }
        return types;
      }
    } catch(Exception ex) {
      log.error(ex.getMessage(), ex);
    }
    return Collections.emptyList();
  }

}

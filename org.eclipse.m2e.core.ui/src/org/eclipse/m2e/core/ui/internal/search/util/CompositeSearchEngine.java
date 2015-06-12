/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *      Sonatype, Inc. - initial API and implementation
 *******************************************************************************/

package org.eclipse.m2e.core.ui.internal.search.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.Assert;

import org.apache.maven.artifact.versioning.DefaultArtifactVersion;


public class CompositeSearchEngine implements SearchEngine {

  private SearchEngine[] searchEngines;

  public CompositeSearchEngine(SearchEngine... searchEngines) {
    Assert.isNotNull(searchEngines);
    this.searchEngines = searchEngines;
  }

  public Collection<String> findGroupIds(final String searchExpression, final Packaging packaging,
      final ArtifactInfo containingArtifact) {
    return aggregateResults(searchEngine -> {
      return searchEngine.findGroupIds(searchExpression, packaging, containingArtifact);
    });
  }

  public Collection<String> findArtifactIds(final String groupId, final String searchExpression,
      final Packaging packaging, final ArtifactInfo containingArtifact) {
    return aggregateResults(searchEngine -> {
      return searchEngine.findArtifactIds(groupId, searchExpression, packaging, containingArtifact);
    });
  }

  public Collection<String> findVersions(final String groupId, final String artifactId, final String searchExpression,
      final Packaging packaging) {
    Collection<String> allResults = aggregateResults(searchEngine -> {
      return searchEngine.findVersions(groupId, artifactId, searchExpression, packaging);
    });
    List<String> sortedResult = new ArrayList<>(allResults);
    Collections.sort(sortedResult, new Comparator<String>() {
      public int compare(String s1, String s2) {
        return new DefaultArtifactVersion(s2).compareTo(new DefaultArtifactVersion(s1));
      }
    });
    return sortedResult;
  }

  public Collection<String> findClassifiers(final String groupId, final String artifactId, final String version,
      final String prefix, final Packaging packaging) {
    return aggregateResults(searchEngine -> {
      return searchEngine.findClassifiers(groupId, artifactId, version, prefix, packaging);
    });
  }

  public Collection<String> findTypes(final String groupId, final String artifactId, final String version,
      final String prefix, final Packaging packaging) {
    return aggregateResults(searchEngine -> {
      return searchEngine.findTypes(groupId, artifactId, version, prefix, packaging);
    });
  }

  private Collection<String> aggregateResults(Query query) {
    Set<String> allResults = new LinkedHashSet<>();
    //XXX it's tempting to use Java 8 stream API to parallelize searches
    // but ForkJoinPool.common() thread pool is limited per JVM.
    // see http://zeroturnaround.com/rebellabs/java-parallel-streams-are-bad-for-your-health/
    // another alternative to investigate is Eclipse's JobGroup API
    for(SearchEngine searchEngine : searchEngines) {
      Collection<String> results = query.execute(searchEngine);
      if(results != null && !results.isEmpty()) {
        allResults.addAll(results);
      }
    }
    return allResults;
  }

  private static interface Query {
    Collection<String> execute(SearchEngine searchEngine);
  }
}

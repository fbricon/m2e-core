/*******************************************************************************
 * Copyright (c) 2008-2015 Sonatype, Inc. and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *      Sonatype, Inc. - initial API and implementation
 *      Fred Bricon (Red Hat, Inc.) - auto update project configuration
 *******************************************************************************/

package org.eclipse.m2e.jdt.ui.internal;

import java.util.Collections;
import java.util.Date;

import org.osgi.framework.BundleContext;

import org.eclipse.core.runtime.Platform;
import org.eclipse.mylyn.commons.notifications.ui.AbstractUiNotification;
import org.eclipse.mylyn.commons.notifications.ui.NotificationsUi;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.internal.events.IMavenEvent;
import org.eclipse.m2e.core.internal.events.IMavenEventListener;
import org.eclipse.m2e.core.internal.events.IMavenEventManager;
import org.eclipse.m2e.core.internal.preferences.MavenConfigurationImpl;


@SuppressWarnings("restriction")
public class MavenJdtUiPluginActivator extends AbstractUIPlugin {

  public class DownloadSourcePreferencesNotification extends AbstractUiNotification {
    public DownloadSourcePreferencesNotification() {
      super(PLUGIN_ID + ".notifications.download.sources");
    }

    public <T> T getAdapter(Class<T> adapter) {
      return Platform.getAdapterManager().getAdapter(this, adapter);
    }

    public String getLabel() {
      return "Click here to always download Maven sources by default";
    }

    public Image getNotificationImage() {
      return null;
    }

    public Image getNotificationKindImage() {
      return null;
    }

    public void open() {
      MavenConfigurationImpl config = (MavenConfigurationImpl) MavenPlugin.getMavenConfiguration();
      if(!config.isDownloadSources()) {
        config.setDownloadSources(true);
      }
    }

    public Date getDate() {
      return null;
    }

    public String getDescription() {
      return null;
    }

    public Object getToken() {
      return null;
    }
  }

  public static final String PLUGIN_ID = "org.eclipse.m2e.jdt.ui"; //$NON-NLS-1$

  private static MavenJdtUiPluginActivator instance;

  private IMavenEventListener mavenEventListener;

  @Override
  public void start(BundleContext context) throws Exception {
    super.start(context);
    mavenEventListener = new IMavenEventListener() {
      public void listen(IMavenEvent event) {
        if(MavenPlugin.getMavenConfiguration().isDownloadSources()) {
          return;
        }
        Display.getDefault().asyncExec(() -> {
          NotificationsUi.getService().notify(Collections.singletonList(new DownloadSourcePreferencesNotification()));
        });
        //IMavenEventManager.getDefault().unregisterListener(event.getType(), this);
      }
    };
    IMavenEventManager.getDefault().registerListener("disabled.sources.download", mavenEventListener);
  }

  @Override
  public void stop(BundleContext context) throws Exception {
    super.stop(context);

  }

  public static MavenJdtUiPluginActivator getDefault() {
    return instance;
  }
}

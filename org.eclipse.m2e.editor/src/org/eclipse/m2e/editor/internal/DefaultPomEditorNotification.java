/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *      Red Hat, Inc. - initial API and implementation
 *******************************************************************************/

package org.eclipse.m2e.editor.internal;

import static org.eclipse.m2e.editor.internal.EditorPreferencesUtil.isOpenXmlEditorByDefault;
import static org.eclipse.m2e.editor.internal.EditorPreferencesUtil.setOpenXmlEditorByDefault;

import java.util.Date;

import org.eclipse.core.runtime.Platform;
import org.eclipse.mylyn.commons.notifications.ui.AbstractUiNotification;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import org.eclipse.m2e.editor.MavenEditorImages;
import org.eclipse.m2e.editor.MavenEditorPlugin;


/**
 * UI Notification for setting the XML Editor page as default POM editor
 *
 * @author Fred Bricon
 */
@SuppressWarnings("restriction")
public class DefaultPomEditorNotification extends AbstractUiNotification {

  private static final String EVENT_ID = MavenEditorPlugin.PLUGIN_ID + ".notifications.default.xml.editor";

  private boolean clicked;

  public DefaultPomEditorNotification() {
    super(EVENT_ID);
  }

  public <T> T getAdapter(Class<T> adapter) {
    return Platform.getAdapterManager().getAdapter(this, adapter);
  }

  public String getLabel() {
    return "Click here to always open the XML editor by default";
  }

  public String getDescription() {
    return null;
  }

  public Date getDate() {
    return null;
  }

  public void open() {
    if(clicked) {
      return;
    }
    clicked = true;
    Display.getCurrent().asyncExec(() -> {
      if(!isOpenXmlEditorByDefault()) {
        setOpenXmlEditorByDefault(true);
      }
    });
  }

  public Image getNotificationKindImage() {
    return MavenEditorImages.IMG_M2E;
  }

  public Image getNotificationImage() {
    return null;
  }

}

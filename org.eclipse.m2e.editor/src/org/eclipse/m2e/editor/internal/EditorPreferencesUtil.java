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

import org.eclipse.m2e.core.internal.preferences.MavenPreferenceConstants;
import org.eclipse.m2e.core.ui.internal.M2EUIPluginActivator;


/**
 * Editor Preference utility class
 *
 * @author Fred Bricon
 */
@SuppressWarnings("restriction")
public class EditorPreferencesUtil {

  private EditorPreferencesUtil() {
    //no instantiation
  }

  public static boolean isOpenXmlEditorByDefault() {
    return getBooleanPreference(MavenPreferenceConstants.P_DEFAULT_POM_EDITOR_PAGE);
  }

  public static void setOpenXmlEditorByDefault(boolean value) {
    setPreference(MavenPreferenceConstants.P_DEFAULT_POM_EDITOR_PAGE, value);
  }

  public static boolean isDisplayXmlEditorNotification() {
    return getBooleanPreference(MavenPreferenceConstants.P_DISPLAY_XML_EDITOR_NOTIFICATION);
  }

  public static void setDisplayXmlEditorNotification(boolean value) {
    setPreference(MavenPreferenceConstants.P_DISPLAY_XML_EDITOR_NOTIFICATION, value);
  }

  private static void setPreference(String key, boolean value) {
    M2EUIPluginActivator.getDefault().getPreferenceStore().setValue(key, value);
  }

  private static boolean getBooleanPreference(String key) {
    return M2EUIPluginActivator.getDefault().getPreferenceStore().getBoolean(key);
  }
}

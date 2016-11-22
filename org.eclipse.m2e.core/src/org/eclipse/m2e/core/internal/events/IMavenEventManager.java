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
package org.eclipse.m2e.core.internal.events;

import org.eclipse.m2e.core.internal.MavenPluginActivator;


/**
 * A {@link IMavenEvent} manager to which {@link IMavenEventListener}s can be attached.
 *
 * @author Fred Bricon
 */
public interface IMavenEventManager {

  /**
   * @return the default {@link IMavenEventManager}
   */
  static IMavenEventManager getDefault() {
    return MavenPluginActivator.getDefault().getMavenEventManager();
  }
  
  /**
   * Fires an {@link IMavenEvent} event
   * 
   * @param event an {@link IMavenEvent}, must not be <code>null</code>
   */
  void fire(IMavenEvent event);

  /**
   * Registers an {@link IMavenEventListener} against an {@link IMavenEvent}'s type
   * 
   * @param eventType the type of an {@link IMavenEvent}, must not be <code>null</code>
   * @param listener an {@link IMavenEvent} to register, must not be <code>null</code>
   */
  void registerListener(String eventType, IMavenEventListener listener);

  /**
   * Unregister an {@link IMavenEventListener} from an {@link IMavenEvent}'s type
   * 
   * @param eventType the type of an {@link IMavenEvent}, must not be <code>null</code>
   * @param listener an {@link IMavenEvent} to unregister, must not be <code>null</code>
   */
  void unregisterListener(String eventType, IMavenEventListener listener);
}

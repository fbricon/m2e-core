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

/**
 * Basic Maven Event
 *
 * @author Fred Bricon
 */
public interface IMavenEvent {

  /**
   * @return the type of event
   */
  String getType();

  /**
   * @return the source of the event, may be <code>null</code>;
   */
  Object getSource();
}

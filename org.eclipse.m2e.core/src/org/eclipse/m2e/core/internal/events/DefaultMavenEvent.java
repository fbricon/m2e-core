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
 * Default {@link IMavenEvent} implementation
 *
 * @author Fred Bricon
 */
public class DefaultMavenEvent implements IMavenEvent {

  private String type;

  private Object source;

  public DefaultMavenEvent(String type) {
    this.type = type;
  }

  public DefaultMavenEvent(String type, Object source) {
    this(type);
    this.source = source;
  }

  public String getType() {
    return type;
  }

  public Object getSource() {
    return source;
  }

}

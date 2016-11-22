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

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.eclipse.core.runtime.Assert;


/**
 * Manages {@link IMavenEvent}s and {@link IMavenEventListener}s
 *
 * @author Fred Bricon
 */
public class MavenEventManagerImpl implements IMavenEventManager {

  private static final Logger log = LoggerFactory.getLogger(MavenEventManagerImpl.class);

  private Map<String, Set<IMavenEventListener>> listenersMap = new ConcurrentHashMap<>();

  public void fire(IMavenEvent event) {
    Assert.isNotNull(event, "Event must not be null");
    Set<IMavenEventListener> listeners = listenersMap.get(event.getType());
    if(listeners == null || listeners.isEmpty()) {
      return;
    }
    for(IMavenEventListener listener : listeners) {
      try {
        log.debug("firing " + event.getType() + " to " + listener.getClass());
        listener.listen(event);
      } catch(Exception e) {
        log.error("Error invoking listener " + listener.getClass(), e);
      }
    }
  }

  public void registerListener(String eventType, IMavenEventListener listener) {
    Assert.isNotNull(eventType, "Event type must not be null");
    Assert.isNotNull(listener, "Listener must not be null");
    Set<IMavenEventListener> listeners = listenersMap.get(eventType);
    if(listeners == null) {
      listeners = new LinkedHashSet<>();
      listenersMap.put(eventType, listeners);
    }
    listeners.add(listener);
    log.debug("Registered " + listener.getClass() + " to " + eventType);
  }

  public void unregisterListener(String eventType, IMavenEventListener listener) {
    Assert.isNotNull(eventType, "Event type must not be null");
    Assert.isNotNull(listener, "Listener must not be null");
    Set<IMavenEventListener> listeners = listenersMap.get(eventType);
    if(listeners != null) {
      listeners.remove(listener);
      log.debug("Unregistered " + listener.getClass() + " from " + eventType);
    }
  }


}

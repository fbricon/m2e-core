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

import java.util.EventListener;


/**
 * Classes which implement this interface will be notified of {@link IMavenEvent} events that are generated at the
 * workspace level. Listeners must be registered to
 * {@link IMavenEventManager#registerListener(String, IMavenEventListener)}.
 *
 * @author Fred Bricon
 */
public interface IMavenEventListener extends EventListener {

  void listen(IMavenEvent event);
}

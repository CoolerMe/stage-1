/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.geektimes.context.repository;

import org.geektimes.commons.function.ThrowableAction;
import org.geektimes.commons.function.ThrowableFunction;

import javax.naming.*;
import java.util.LinkedHashSet;
import java.util.Set;

import static java.util.Collections.emptySet;

/**
 * JNDI {@link ComponentRepository}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class JndiComponentContext extends AbstractComponentRepository {

    private static final String COMPONENT_ENV_CONTEXT_NAME = "java:comp/env";

    private Context envContext; // Component Env Context

    private ClassLoader classLoader;

    @Override
    public void initialize() {
        initClassLoader();
        initEnvContext();
    }

    private void initClassLoader() {
        if (classLoader == null) {
            classLoader = Thread.currentThread().getContextClassLoader();
        }
    }

    private void initEnvContext() throws RuntimeException {
        if (this.envContext != null) {
            return;
        }
        Context context = null;
        try {
            context = new InitialContext();
            this.envContext = (Context) context.lookup(COMPONENT_ENV_CONTEXT_NAME);
        } catch (NamingException e) {
            throw new RuntimeException(e);
        } finally {
            close(context);
        }
    }

    @Override
    public void registerComponent(String name, Object component) {
        executeInContext(context -> {
            context.bind(name, component);
            return null;
        });
    }

    /**
     * ??? Context ???????????????????????? ThrowableFunction ??????????????????
     *
     * @param function ThrowableFunction
     * @param <R>      ??????????????????
     * @return ??????
     * @see ThrowableFunction#apply(Object)
     */
    protected <R> R executeInContext(ThrowableFunction<Context, R> function) {
        return executeInContext(this.envContext, function, true);
    }

    @Override
    protected Set<String> listComponentNames() {
        return listComponentNames("/");
    }

    protected Set<String> listComponentNames(String name) {
        return executeInContext(context -> {
            NamingEnumeration<NameClassPair> e = executeInContext(context, ctx -> ctx.list(name));

            // ?????? - Context
            // ?????? -
            if (e == null) { // ?????? JNDI ????????????????????????
                return emptySet();
            }

            Set<String> allComponentNames = new LinkedHashSet<>();
            while (e.hasMoreElements()) {
                NameClassPair element = e.nextElement();
                String className = element.getClassName();
                Class<?> targetClass = classLoader.loadClass(className);
                if (Context.class.isAssignableFrom(targetClass)) {
                    // ??????????????????????????????Context ?????????????????????????????????
                    allComponentNames.addAll(listComponentNames(element.getName()));
                } else {
                    // ??????????????????????????????????????????????????????????????????????????????
                    String fullName = name.startsWith("/") ?
                            element.getName() : name + "/" + element.getName();
                    allComponentNames.add(fullName);
                }
            }
            return allComponentNames;
        });
    }

    @Override
    protected <C> C loadComponent(String name) {
        return executeInContext(context -> (C) context.lookup(name));
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    protected void doDestroy() {
        closeEnvContext();
    }

    private void closeEnvContext() {
        close(this.envContext);
    }

    private static void close(Context context) {
        if (context != null) {
            ThrowableAction.execute(context::close);
        }
    }
}

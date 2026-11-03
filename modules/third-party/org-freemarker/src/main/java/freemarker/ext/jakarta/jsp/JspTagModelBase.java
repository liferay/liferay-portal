/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package freemarker.ext.jakarta.jsp;

import java.beans.Introspector;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import freemarker.core._DelayedJQuote;
import freemarker.core._DelayedShortClassName;
import freemarker.core._ErrorDescriptionBuilder;
import freemarker.core._TemplateModelException;
import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.jakarta.jsp.SimpleTagDirectiveModel.TemplateExceptionWrapperJspException;
import freemarker.template.ObjectWrapper;
import freemarker.template.ObjectWrapperAndUnwrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.utility.StringUtil;

class JspTagModelBase {
    protected final String tagName;
    private final Class tagClass;
    private final Method dynaSetter;
    private final Map propertySetters = new HashMap();
    
    protected JspTagModelBase(String tagName, Class tagClass) {
        this.tagName = tagName;
        this.tagClass = tagClass;
        for (Method method : tagClass.getMethods()) {
            if (method.getReturnType().equals(void.class)
                && (method.getParameterTypes().length == 1)) {

                String methodName = method.getName();

                if (methodName.startsWith("set") && (methodName.length() > 3) &&
                    Character.isUpperCase(methodName.charAt(3))) {

                    propertySetters.put(
                        Introspector.decapitalize(methodName.substring(3)),
                        method);
                }
            }
        }
        // Check to see if the tag implements the JSP2.0 DynamicAttributes
        // interface, to allow setting of arbitrary attributes
        Method dynaSetter;
        try {
            dynaSetter = tagClass.getMethod("setDynamicAttribute",
                            new Class[] {String.class, String.class, Object.class});
        } catch (NoSuchMethodException nsme) {
            dynaSetter = null;
        }
        this.dynaSetter = dynaSetter;
    }
    
    Object getTagInstance() throws IllegalAccessException, InstantiationException {
        return tagClass.newInstance();
    }
    
    void setupTag(Object tag, Map args, ObjectWrapper wrapper)
    throws TemplateModelException, 
        InvocationTargetException, 
        IllegalAccessException {
        if (args != null && !args.isEmpty()) {
            ObjectWrapperAndUnwrapper unwrapper = 
                    wrapper instanceof ObjectWrapperAndUnwrapper ? (ObjectWrapperAndUnwrapper) wrapper
                            : BeansWrapper.getDefaultInstance();  // [2.4] Throw exception in this case
            final Object[] argArray = new Object[1];
            for (Iterator iter = args.entrySet().iterator(); iter.hasNext(); ) {
                final Map.Entry entry = (Map.Entry) iter.next();
                final Object arg = unwrapper.unwrap((TemplateModel) entry.getValue());
                argArray[0] = arg;
                final Object paramName = entry.getKey();
                Method setterMethod = (Method) propertySetters.get(paramName);
                if (setterMethod == null) {
                    if (dynaSetter == null) {
                        throw new TemplateModelException("Unknown property "
                                + StringUtil.jQuote(paramName.toString())
                                + " on instance of " + tagClass.getName());
                    } else {
                        dynaSetter.invoke(tag, null, paramName, argArray[0]);
                    }
                } else {
                    if (arg instanceof BigDecimal) {
                        argArray[0] = BeansWrapper.coerceBigDecimal(
                                (BigDecimal) arg, setterMethod.getParameterTypes()[0]);
                    }
                    try {
                        setterMethod.invoke(tag, argArray);
                    } catch (Exception e) {
                        final Class setterType = setterMethod.getParameterTypes()[0];
                        final _ErrorDescriptionBuilder desc = new _ErrorDescriptionBuilder(
                                "Failed to set JSP tag parameter ", new _DelayedJQuote(paramName),
                                " (declared type: ", new _DelayedShortClassName(setterType)
                                + ", actual value's type: ",
                                (argArray[0] != null
                                        ? new _DelayedShortClassName(argArray[0].getClass()) : "Null"),
                                "). See cause exception for the more specific cause...");
                        if (e instanceof IllegalArgumentException && !(setterType.isAssignableFrom(String.class))
                                && argArray[0] != null && argArray[0] instanceof String) {
                            desc.tip("This problem is often caused by unnecessary parameter quotation. Parameters "
                                    + "aren't quoted in FTL, similarly as they aren't quoted in most languages. "
                                    + "For example, these parameter assignments are wrong: ",
                                    "<@my.tag p1=\"true\" p2=\"10\" p3=\"${someVariable}\" p4=\"${x+1}\" />",
                                    ". The correct form is: ",
                                    "<@my.tag p1=true p2=10 p3=someVariable p4=x+1 />",
                                    ". Only string literals are quoted (regardless of where they occur): ",
                                    "<@my.box style=\"info\" message=\"Hello ${name}!\" width=200 />",
                                    ".");
                        }
                        throw new _TemplateModelException(e, null, desc);
                    }
                }
            }
        }
    }

    protected final TemplateModelException toTemplateModelExceptionOrRethrow(Exception e) throws TemplateModelException {
        if (e instanceof RuntimeException && !isCommonRuntimeException((RuntimeException) e)) {
            throw (RuntimeException) e;
        }
        if (e instanceof TemplateModelException) {
            throw (TemplateModelException) e;
        }
        if (e instanceof TemplateExceptionWrapperJspException) {
            return toTemplateModelExceptionOrRethrow(((TemplateExceptionWrapperJspException) e).getCause());
        }
        return new TemplateModelException(
                "Error while invoking the " + StringUtil.jQuote(tagName) + " JSP custom tag; see cause exception",
                e instanceof TemplateException, e);
    }

    /**
     * Runtime exceptions that we don't want to propagate, instead we warp them into a more helpful exception. These are
     * the ones where it's very unlikely that someone tries to catch specifically these around
     * {@link Template#process(Object, java.io.Writer)}.
     */
    private boolean isCommonRuntimeException(RuntimeException e) {
        final Class eClass = e.getClass();
        // We deliberately don't accept sub-classes. Those are possibly application specific and some want to catch them
        // outside the template.
        return eClass == NullPointerException.class
                || eClass == IllegalArgumentException.class
                || eClass == ClassCastException.class
                || eClass == IndexOutOfBoundsException.class;
    }
    
}
/* @generated */
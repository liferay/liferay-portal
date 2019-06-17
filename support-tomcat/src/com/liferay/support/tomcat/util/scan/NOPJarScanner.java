/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.support.tomcat.util.scan;

import javax.servlet.ServletContext;

import org.apache.tomcat.JarScanFilter;
import org.apache.tomcat.JarScanType;
import org.apache.tomcat.JarScanner;
import org.apache.tomcat.JarScannerCallback;

/**
 * @author Preston Crary
 */
public class NOPJarScanner implements JarScanner {

    @Override
    public JarScanFilter getJarScanFilter() {
        return _jarScanFilter;
    }

    @Override
    public void scan(
            JarScanType jarScanType, ServletContext servletContext,
            JarScannerCallback jarScannerCallback) {
    }

    @Override
    public void setJarScanFilter(JarScanFilter jarScanFilter) {
    }

    private static final JarScanFilter _jarScanFilter = new NOPJarScanFilter();

    private static class NOPJarScanFilter implements JarScanFilter {

        @Override
        public boolean check(JarScanType jarScanType, String jarName) {
            return false;
        }

    }

}

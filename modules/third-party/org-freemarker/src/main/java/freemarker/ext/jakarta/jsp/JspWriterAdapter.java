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

import java.io.IOException;
import java.io.Writer;

import jakarta.servlet.jsp.JspWriter;

import freemarker.template.utility.SecurityUtilities;

class JspWriterAdapter extends JspWriter {
    static final String NEWLINE = SecurityUtilities.getSystemProperty("line.separator", "\n");
    
    private final Writer out;
    
    JspWriterAdapter(Writer out) {
        super(0, true);
        this.out = out;
    }
    
    @Override
    public String toString() {
        return "JspWriterAdapter wrapping a " + out.toString();
    }
    
    @Override
    public void clear() throws IOException {
        throw new IOException("Can't clear");
    }

    @Override
    public void clearBuffer() throws IOException {
        throw new IOException("Can't clear");
    }

    @Override
    public void close() throws IOException {
        throw new IOException("Close not permitted.");
    }

    @Override
    public void flush() throws IOException {
        out.flush();
    }

    @Override
    public int getRemaining() {
        return 0;
    }

    @Override
    public void newLine() throws IOException {
        out.write(NEWLINE);
    }

    @Override
    public void print(boolean arg0) throws IOException {
        out.write(arg0 ? Boolean.TRUE.toString() : Boolean.FALSE.toString());
    }

    @Override
    public void print(char arg0) throws IOException {
        out.write(arg0);
    }

    @Override
    public void print(char[] arg0) throws IOException {
        out.write(arg0);
    }

    @Override
    public void print(double arg0) throws IOException {
        out.write(Double.toString(arg0));
    }

    @Override
    public void print(float arg0) throws IOException {
        out.write(Float.toString(arg0));
    }

    @Override
    public void print(int arg0) throws IOException {
        out.write(Integer.toString(arg0));
    }

    @Override
    public void print(long arg0) throws IOException {
        out.write(Long.toString(arg0));
    }

    @Override
    public void print(Object arg0) throws IOException {
        out.write(arg0 == null ? "null" : arg0.toString());
    }

    @Override
    public void print(String arg0) throws IOException {
        out.write(arg0);
    }

    @Override
    public void println() throws IOException {
        newLine();
    }

    @Override
    public void println(boolean arg0) throws IOException {
        print(arg0);
        newLine();
    }

    @Override
    public void println(char arg0) throws IOException {
        print(arg0);
        newLine();
    }

    @Override
    public void println(char[] arg0) throws IOException {
        print(arg0);
        newLine();
    }

    @Override
    public void println(double arg0) throws IOException {
        print(arg0);
        newLine();
    }

    @Override
    public void println(float arg0) throws IOException {
        print(arg0);
        newLine();
    }

    @Override
    public void println(int arg0) throws IOException {
        print(arg0);
        newLine();
    }

    @Override
    public void println(long arg0) throws IOException {
        print(arg0);
        newLine();
    }

    @Override
    public void println(Object arg0) throws IOException {
        print(arg0);
        newLine();
    }

    @Override
    public void println(String arg0) throws IOException {
        print(arg0);
        newLine();
    }

    @Override
    public void write(int c) throws IOException {
        out.write(c);
    }
    
    @Override
    public void write(char[] arg0, int arg1, int arg2)
        throws IOException {
        out.write(arg0, arg1, arg2);
    }

    @Override
    public void write(String s) throws IOException {
        out.write(s);
    }

    @Override
    public void write(String s, int offset, int length) throws IOException {
        out.write(s, offset, length);
    }
}
/* @generated */
/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.cxf.attachment;

import java.io.IOException;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.activation.DataHandler;

import org.apache.cxf.message.Attachment;

public class LazyAttachmentCollection
    implements Collection<Attachment> {

    private AttachmentDeserializer deserializer;
    private final List<Attachment> attachments = new ArrayList<>();
    private final int maxAttachmentCount;

    public LazyAttachmentCollection(AttachmentDeserializer deserializer, int maxAttachmentCount) {
        super();
        this.deserializer = deserializer;
        this.maxAttachmentCount = maxAttachmentCount;
    }

    public List<Attachment> getLoadedAttachments() {
        return attachments;
    }

    private void loadAll() {
        try {
            Attachment a = deserializer.readNext();
            int count = 0;
            while (a != null) {
                attachments.add(a);
                count++;
                if (count > maxAttachmentCount) {
                    throw new IOException("The message contains more attachments than are permitted");
                }
                a = deserializer.readNext();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Check for more attachments by attempting to deserialize the next attachment.
     *
     * @param shouldLoadNew if <i>false</i>, the "loaded attachments" List will not be changed.
     * @return there is more attachment or not
     * @throws IOException
     */
    public boolean hasNext(boolean shouldLoadNew) throws IOException {
        if (shouldLoadNew) {
            if (attachments.size() > maxAttachmentCount) {
                throw new IOException("The message contains more attachments than are permitted");
            }

            Attachment a = deserializer.readNext();
            if (a != null) {
                attachments.add(a);
                return true;
            }
            return false;
        }
        return deserializer.hasNext();
    }

    public boolean hasNext() throws IOException {
        return hasNext(true);
    }
    public Iterator<Attachment> iterator() {
        // CHECKSTYLE:OFF
        return new Iterator<Attachment>() {
            int current;
            boolean removed;

            public boolean hasNext() {
                if (attachments.size() > current) {
                    return true;
                }

                // check if there is another attachment
                try {
                    if (attachments.size() > maxAttachmentCount) {
                        throw new IOException("The message contains more attachments than are permitted");
                    }
                    Attachment a = deserializer.readNext();
                    if (a == null) {
                        return false;
                    }
                    attachments.add(a);
                    return true;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public Attachment next() {
                Attachment a = attachments.get(current);
                current++;
                removed = false;
                return a;
            }

            @Override
            public void remove() {
                if (removed) {
                    throw new IllegalStateException();
                }
                attachments.remove(--current);
                removed = true;
            }
        };
        // CHECKSTYLE:ON
    }

    public int size() {
        loadAll();

        return attachments.size();
    }

    public boolean add(Attachment arg0) {
        if (attachments.size() > maxAttachmentCount) {
            throw new RuntimeException(new IOException("The message contains more attachments than are permitted"));
        }
        return attachments.add(arg0);
    }

    public boolean addAll(Collection<? extends Attachment> arg0) {
        if (attachments.size() + arg0.size() > maxAttachmentCount) {
            throw new RuntimeException(new IOException("The message contains more attachments than are permitted"));
        }
        return attachments.addAll(arg0);
    }

    public void clear() {
        attachments.clear();
    }

    public boolean contains(Object arg0) {
        return attachments.contains(arg0);
    }

    public boolean containsAll(Collection<?> arg0) {
        return attachments.containsAll(arg0);
    }

    public boolean isEmpty() {
        if (attachments.isEmpty()) {
            return !iterator().hasNext();
        }
        return attachments.isEmpty();
    }

    public boolean remove(Object arg0) {
        return attachments.remove(arg0);
    }

    public boolean removeAll(Collection<?> arg0) {
        return attachments.removeAll(arg0);
    }

    public boolean retainAll(Collection<?> arg0) {
        return attachments.retainAll(arg0);
    }

    public Object[] toArray() {
        loadAll();

        return attachments.toArray();
    }

    public <T> T[] toArray(T[] arg0) {
        loadAll();

        return attachments.toArray(arg0);
    }

    public Map<String, DataHandler> createDataHandlerMap() {
        return new LazyAttachmentMap(this);
    }

    private static class LazyAttachmentMap implements Map<String, DataHandler> {
        LazyAttachmentCollection collection;

        LazyAttachmentMap(LazyAttachmentCollection c) {
            collection = c;
        }

        public void clear() {
            collection.clear();
        }

        public boolean containsKey(Object key) {
            Iterator<Attachment> it = collection.iterator();
            while (it.hasNext()) {
                Attachment at = it.next();
                if (key.equals(at.getId())) {
                    return true;
                }
            }
            return false;
        }

        public boolean containsValue(Object value) {
            Iterator<Attachment> it = collection.iterator();
            while (it.hasNext()) {
                Attachment at = it.next();
                if (value.equals(at.getDataHandler())) {
                    return true;
                }
            }
            return false;
        }

        public DataHandler get(Object key) {
            Iterator<Attachment> it = collection.iterator();
            while (it.hasNext()) {
                Attachment at = it.next();
                if (key.equals(at.getId())) {
                    return at.getDataHandler();
                }
            }
            return null;
        }

        public boolean isEmpty() {
            return collection.isEmpty();
        }
        public int size() {
            return collection.size();
        }

        public DataHandler remove(Object key) {
            Iterator<Attachment> it = collection.iterator();
            while (it.hasNext()) {
                Attachment at = it.next();
                if (key.equals(at.getId())) {
                    collection.remove(at);
                    return at.getDataHandler();
                }
            }
            return null;
        }
        public DataHandler put(String key, DataHandler value) {
            Attachment at = new AttachmentImpl(key, value);
            collection.add(at);
            return value;
        }

        public void putAll(Map<? extends String, ? extends DataHandler> t) {
            for (Map.Entry<? extends String, ? extends DataHandler> ent : t.entrySet()) {
                put(ent.getKey(), ent.getValue());
            }
        }


        public Set<Map.Entry<String, DataHandler>> entrySet() {
            return new AbstractSet<Map.Entry<String, DataHandler>>() {
                public Iterator<Map.Entry<String, DataHandler>> iterator() {
                    return new Iterator<Map.Entry<String, DataHandler>>() {
                        Iterator<Attachment> it = collection.iterator();
                        public boolean hasNext() {
                            return it.hasNext();
                        }
                        public Map.Entry<String, DataHandler> next() {
                            return new Map.Entry<String, DataHandler>() {
                                Attachment at = it.next();
                                public String getKey() {
                                    return at.getId();
                                }
                                public DataHandler getValue() {
                                    return at.getDataHandler();
                                }
                                public DataHandler setValue(DataHandler value) {
                                    if (at instanceof AttachmentImpl) {
                                        DataHandler h = at.getDataHandler();
                                        ((AttachmentImpl)at).setDataHandler(value);
                                        return h;
                                    }
                                    throw new UnsupportedOperationException();
                                }
                            };
                        }
                        @Override
                        public void remove() {
                            it.remove();
                        }
                    };
                }
                public int size() {
                    return collection.size();
                }
            };
        }

        public Set<String> keySet() {
            return new AbstractSet<String>() {
                public Iterator<String> iterator() {
                    return new Iterator<String>() {
                        Iterator<Attachment> it = collection.iterator();
                        public boolean hasNext() {
                            return it.hasNext();
                        }

                        public String next() {
                            return it.next().getId();
                        }

                        @Override
                        public void remove() {
                            it.remove();
                        }
                    };
                }

                public int size() {
                    return collection.size();
                }
            };
        }


        public Collection<DataHandler> values() {
            return new AbstractCollection<DataHandler>() {
                public Iterator<DataHandler> iterator() {
                    return new Iterator<DataHandler>() {
                        Iterator<Attachment> it = collection.iterator();
                        public boolean hasNext() {
                            return it.hasNext();
                        }
                        public DataHandler next() {
                            return it.next().getDataHandler();
                        }
                        @Override
                        public void remove() {
                            it.remove();
                        }
                    };
                }

                public int size() {
                    return collection.size();
                }
            };
        }

    }


}
/* @generated */

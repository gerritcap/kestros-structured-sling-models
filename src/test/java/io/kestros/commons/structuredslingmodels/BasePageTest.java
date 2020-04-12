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

package io.kestros.commons.structuredslingmodels;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class BasePageTest {

    @Rule
    public final SlingContext context = new SlingContext();

    private Resource resource;

    private BasePage basePage;

    private Map<String, Object> properties = new HashMap<>();

    @Before
    public void setUp() {
        context.addModelsForPackage("io.kestros");
        context.create().resource("/content");
    }

    @Test
    public void testGetName() {
        resource = context.create().resource("/content/page");

        basePage = resource.adaptTo(BasePage.class);

        assertEquals("page", Objects.requireNonNull(basePage).getName());
    }

    @Test
    public void testGetNameWhenAdaptedFromJcrContent() {
        context.create().resource("/content/page");
        resource = context.create().resource("/content/page/jcr:content");

        basePage = resource.adaptTo(BasePage.class);

        assertEquals("page", Objects.requireNonNull(basePage).getName());
    }

    @Test
    public void testGetNameWhenAdaptedFromJcrContentAtRootLevel() {
        resource = context.create().resource("/jcr:content");

        basePage = resource.adaptTo(BasePage.class);

        assertEquals("", Objects.requireNonNull(basePage).getName());
    }

    @Test
    public void testGetNameWhenAdaptedFromJcrContentWhenParentIsNull() {
        resource = context.create().resource("/jcr:content");
        resource = spy(resource);

        given(resource.getParent()).willReturn(null);

        basePage = resource.adaptTo(BasePage.class);

        assertEquals("jcr:content", Objects.requireNonNull(basePage).getName());
    }


    @Test
    public void testGetPath() {
        resource = context.create().resource("/content/page");
        context.create().resource("/content/page/jcr:content");

        basePage = resource.adaptTo(BasePage.class);

        assertEquals("/content/page", Objects.requireNonNull(basePage).getPath());
    }

    @Test
    public void testGetPathWhenAdaptedFromJcrContent() {
        resource = context.create().resource("/content/page");
        resource = context.create().resource("/content/page/jcr:content");

        basePage = resource.adaptTo(BasePage.class);

        assertEquals("/content/page", Objects.requireNonNull(basePage).getPath());
    }

    @Test
    public void testGetPathWhenAdaptedFromJcrContentAtRootLevel() {
        resource = context.create().resource("/jcr:content");

        basePage = resource.adaptTo(BasePage.class);

        assertEquals("/", Objects.requireNonNull(basePage).getPath());
    }

    @Test
    public void testGetPathWhenAdaptedFromJcrContentWhenParentIsNull() {
        resource = context.create().resource("/jcr:content");
        resource = spy(resource);

        given(resource.getParent()).willReturn(null);

        basePage = resource.adaptTo(BasePage.class);

        assertEquals("/jcr:content", Objects.requireNonNull(basePage).getPath());
    }


    @Test
    public void testGetTitle() {
        properties.put("jcr:title", "Page Title");

        resource = context.create().resource("/content/page-with-title");
        resource = context.create().resource("/content/page-with-title/jcr:content", properties);

        basePage = resource.adaptTo(BasePage.class);

        assertEquals("Page Title", Objects.requireNonNull(basePage).getTitle());
    }

    @Test
    public void testGetTitleWhenNoneSet() {
        properties.put("jcr:title", "Page Title");

        resource = context.create().resource("/content/page-without-title");

        basePage = resource.adaptTo(BasePage.class);

        assertEquals("page-without-title", Objects.requireNonNull(basePage).getTitle());
    }

    @Test
    public void testGetProperties() {
        properties.put("jcr:title", "Page Title");

        resource = context.create().resource("/content/page-with-properties");
        context.create().resource("/content/page-with-properties/jcr:content", properties);

        basePage = resource.adaptTo(BasePage.class);

        assertNull(Objects.requireNonNull(basePage).getResource().getValueMap().get("jcr:title"));
        assertEquals(properties, basePage.getProperties());
    }

    @Test
    public void testGetPropertiesWhenAdaptedFromJcrContent() {
        properties.put("jcr:title", "Page Title");

        context.create().resource("/content/page-with-properties");
        resource = context.create().resource("/content/page-with-properties/jcr:content", properties);

        basePage = resource.adaptTo(BasePage.class);

        assertEquals("Page Title", Objects.requireNonNull(basePage).getProperties().get("jcr:title"));
        assertEquals(properties, basePage.getProperties());
    }

    @Test
    public void testGetPropertiesWhenNoJcrContent() {
        properties.put("jcr:title", "Page Title");

        resource = context.create().resource("/content/page-with-properties", properties);

        basePage = resource.adaptTo(BasePage.class);
        basePage = Mockito.spy(Objects.requireNonNull(basePage));

        assertEquals(properties, basePage.getProperties());
        verify(basePage, times(1)).getPath();
    }

}
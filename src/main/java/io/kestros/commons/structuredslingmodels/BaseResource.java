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

import static org.apache.jackrabbit.JcrConstants.JCR_LASTMODIFIED;
import static org.apache.jackrabbit.vault.util.JcrConstants.JCR_DESCRIPTION;
import static org.apache.jackrabbit.vault.util.JcrConstants.JCR_TITLE;
import static org.apache.sling.api.resource.ResourceResolver.PROPERTY_RESOURCE_TYPE;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.kestros.commons.structuredslingmodels.annotation.Property;
import io.kestros.commons.structuredslingmodels.annotation.StructuredModel;
import io.kestros.commons.structuredslingmodels.exceptions.NoParentResourceException;
import io.kestros.commons.structuredslingmodels.utils.SlingModelUtils;
import java.util.Date;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;

/**
 * Baseline Sling Model to extend all Resource Models from.
 */
@StructuredModel(docPaths = {
    "/content/guide-articles/kestros/structured-models/extending-base-resource",
    "/content/guide-articles/kestros/structured-models/model-validation",
    "/content/guide-articles/kestros/structured-models/using-sling-model-utils",
    "/content/guide-articles/kestros/structured-models/using-common-validators"})
@Model(adaptables = Resource.class,
       resourceType = "sling/servlet/default")
public class BaseResource extends BaseSlingModel {

  /**
   * Original Resource the current Model was adapted from.
   */
  @Self
  private Resource resource;

  /**
   * Original Resource the current Model was adapted from.
   *
   * @return Original Resource the current Model was adapted from.
   */
  @JsonIgnore
  @Nonnull
  public Resource getResource() {
    return resource;
  }

  /**
   * Parent Resource, adapted to BaseResource.
   *
   * @return Parent Resource, adapted to BaseResource.
   * @throws NoParentResourceException No parent was found. Should only happen on the `/`
   *     resource.
   */
  @JsonIgnore
  public BaseResource getParent() throws NoParentResourceException {
    return SlingModelUtils.getParentResourceAsBaseResource(this);
  }

  /**
   * Resource name the current model was adapted from.
   *
   * @return Resource name the current model was adapted from.
   */
  @Nonnull
  @Property(description = "Name of the current Resource.")
  public String getName() {
    return resource.getName();
  }

  /**
   * Path to the resource the current model was adapted from.
   *
   * @return Path to the resource the current model was adapted from.
   */
  @Nonnull
  @Property(description = "Path to the current Resource.")
  public String getPath() {
    return resource.getPath();
  }

  /**
   * Path to the current Resource, with all `/` characters replaced with `-` characters.
   *
   * @return Path to the current Resource, with all `/` characters replaced with `-` characters.
   */
  @Nonnull
  @Property(description = "Path to the current Resource, with all `/` characters replaced with "
                          + "`-` characters.")
  public String getPathWithReplacedSlashes() {
    return getPath().replaceAll("/", "-");
  }

  /**
   * Property ValueMap associated to the resource the current model was adapted from.
   *
   * @return Property ValueMap associated to the resource the current model was adapted from.
   */
  @JsonIgnore
  @Nonnull
  public ValueMap getProperties() {
    return getResource().getValueMap();
  }

  /**
   * Property value, or the default value.
   *
   * @param key Property to retrieve
   * @param defaultValue Value to return if no matching property is found.
   * @return Property value, or the default value.
   */
  @Nullable
  public <T> T getProperty(@Nonnull String key, T defaultValue) {
    return getProperties().get(key, defaultValue);
  }

  /**
   * Pull the value from jcr:content, or the Resource name if jcr:title is empty or null.
   *
   * @return Title of the current Resource.
   */
  @Nonnull
  @Property(description = "Title of the current resource.")
  public String getTitle() {
    return getProperty(JCR_TITLE, getName());
  }

  /**
   * Description of the current Resource.
   *
   * @return Description of the current Resource.
   */
  @Nonnull
  @Property(description = "Description of the current Resource.")
  public String getDescription() {
    return getProperty(JCR_DESCRIPTION, StringUtils.EMPTY);
  }

  /**
   * Looks to sling:resourceType first, if that is empty, then jcr:primaryType.
   *
   * @return ResourceType of the current Resource.
   */
  @Nonnull
  @Property(description = "ResourceType the current resource will be displayed as when requested.")
  public String getResourceType() {
    if (StringUtils.isEmpty(getSlingResourceType())) {
      return getJcrPrimaryType();
    }
    return getSlingResourceType();
  }

  public String getJcrPrimaryType() {
    return getProperty("jcr:primaryType", StringUtils.EMPTY);
  }

  /**
   * sling:resourceType property of the current Resource, or an empty String.
   *
   * @return sling:resourceType property of the current Resource, or an empty String.
   */
  @Nonnull
  @JsonIgnore
  public String getSlingResourceType() {
    return getProperty(PROPERTY_RESOURCE_TYPE, StringUtils.EMPTY);
  }

  /**
   * sling:resourceSuperType property of the current Resource.
   *
   * @return SuperType of the current Resource, or an empty String.
   */
  @JsonIgnore
  @Nonnull
  public String getResourceSuperType() {
    String resourceSuperType = getResource().getResourceSuperType();
    if (resourceSuperType != null) {
      return resourceSuperType;
    }
    return StringUtils.EMPTY;
  }

  /**
   * ResourceResolver associated to the current Resource.
   *
   * @return ResourceResolver associated to the current Resource.
   */
  @Nonnull
  @JsonIgnore
  public final ResourceResolver getResourceResolver() {
    return getResource().getResourceResolver();
  }

  /**
   * Date when the current Resource was last modified, or null if the property is not found.
   *
   * @return Date when the current Resource was last modified.
   */
  @Nullable
  @JsonIgnore
  public Date getLastModifiedDate() {
    return getProperties().get(JCR_LASTMODIFIED, Date.class);
  }
}

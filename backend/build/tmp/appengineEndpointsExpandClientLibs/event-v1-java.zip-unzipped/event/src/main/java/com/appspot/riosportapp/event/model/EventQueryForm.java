/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
/*
 * This code was generated by https://code.google.com/p/google-apis-client-generator/
 * (build: 2015-03-26 20:30:19 UTC)
 * on 2015-06-20 at 02:11:19 UTC 
 * Modify at your own risk.
 */

package com.appspot.riosportapp.event.model;

/**
 * Model definition for EventQueryForm.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the event. For a detailed explanation see:
 * <a href="http://code.google.com/p/google-http-java-client/wiki/JSON">http://code.google.com/p/google-http-java-client/wiki/JSON</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class EventQueryForm extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.util.List<Filter> filters;

  static {
    // hack to force ProGuard to consider Filter used, since otherwise it would be stripped out
    // see http://code.google.com/p/google-api-java-client/issues/detail?id=528
    com.google.api.client.util.Data.nullOf(Filter.class);
  }

  /**
   * @return value or {@code null} for none
   */
  public java.util.List<Filter> getFilters() {
    return filters;
  }

  /**
   * @param filters filters or {@code null} for none
   */
  public EventQueryForm setFilters(java.util.List<Filter> filters) {
    this.filters = filters;
    return this;
  }

  @Override
  public EventQueryForm set(String fieldName, Object value) {
    return (EventQueryForm) super.set(fieldName, value);
  }

  @Override
  public EventQueryForm clone() {
    return (EventQueryForm) super.clone();
  }

}

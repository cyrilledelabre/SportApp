/* Copyright 2014 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cyrilledelabre.riosportapp.utils;

import com.appspot.riosportapp.event.model.Event;

/**
 * A wrapper around Event {@link com.appspot.riosportapp.event.model.Event}
 * to enable adding additional fields and operations.
 */

public class DecoratedEvent{

    private Event mEvent;
    private boolean mRegistered;

    public DecoratedEvent(Event event, boolean registered) {
        mEvent = event;
        mRegistered = registered;
    }
    public Event getEvent() {
        return mEvent;
    }

    public void setEvent(Event event) {
        mEvent = event;
    }

    public boolean isRegistered() {
        return mRegistered;
    }

    public void setRegistered(boolean registered) {
        mRegistered = registered;
    }

}



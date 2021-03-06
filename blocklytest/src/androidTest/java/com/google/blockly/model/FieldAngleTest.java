/*
 *  Copyright 2016 Google Inc. All Rights Reserved.
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.google.blockly.model;

import android.test.AndroidTestCase;

/**
 * Tests for {@link FieldAngle}.
 */
public class FieldAngleTest extends AndroidTestCase {
    public void testConstructor() {
        FieldAngle field = new FieldAngle("name", 0);
        assertEquals(Field.TYPE_ANGLE, field.getType());
        assertEquals("name", field.getName());
        assertEquals(0f, field.getAngle());
    }

    public void testWrapAround() {
        FieldAngle field = new FieldAngle("name", 360);
        assertEquals(0f, field.getAngle());

        field.setAngle(720f);
        assertEquals(0f, field.getAngle());

        field.setAngle(-180f);
        assertEquals(180f, field.getAngle());

        field.setAngle(10000f);
        assertEquals(280f, field.getAngle());

        field.setAngle(-10000f);
        assertEquals(80f, field.getAngle());

        field.setAngle(27f);
        assertEquals(27f, field.getAngle());

        field.setAngle(-10001f);
        assertEquals(79f, field.getAngle());
    }

    public void testParseValue() {
        FieldAngle field = new FieldAngle("name", 0);

        // xml parsing
        assertTrue(field.setFromString("-180"));
        assertEquals(180f, field.getAngle());
        assertTrue(field.setFromString("27"));
        assertEquals(27f, field.getAngle());
        assertFalse(field.setFromString("this is not a number"));
    }

    public void testClone() {
        FieldAngle field = new FieldAngle("name", 5);
        FieldAngle clone = field.clone();

        assertNotSame(field, field.clone());
        assertEquals(field.getName(), clone.getName());
        assertEquals(field.getAngle(), clone.getAngle());
    }

    public void testObserverEvent() {
        FieldTestHelper.testObserverEvent(new FieldAngle("ANGLE", 0),
                /* New value to assign */ "15",
                /* oldValue */ "0",
                /* newValue */ "15");
        FieldTestHelper.testObserverNoEvent(new FieldAngle("ANGLE", 0));
        FieldTestHelper.testObserverNoEvent(new FieldAngle("ANGLE", 0), "360");
    }
}

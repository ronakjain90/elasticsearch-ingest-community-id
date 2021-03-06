/*
 * Copyright [2018] [Ronak Gothi]
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
 *
 */

package org.elasticsearch.plugin.ingest.community.id;

import org.elasticsearch.ingest.IngestDocument;
import org.elasticsearch.ingest.RandomDocumentPicks;
import org.elasticsearch.test.ESTestCase;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.is;

public class CommunityIdProcessorTests extends ESTestCase {

    public void testThatProcessorWorksWithStringPort() throws Exception {
        Map<String, Object> document = new HashMap<>();
        document.put("source_ip", "192.168.1.52");
        document.put("source_port", "54585");
        document.put("destination_ip", "8.8.8.8");
        document.put("destination_port", "53");
        document.put("transport", "UDP");

        IngestDocument ingestDocument = RandomDocumentPicks.randomIngestDocument(random(), document);

        List<String> field = new ArrayList <String>();
        field.add("source_ip");
        field.add("source_port");
        field.add("destination_ip");
        field.add("destination_port");
        field.add("transport");

        CommunityIdProcessor processor = new CommunityIdProcessor(randomAlphaOfLength(10), field, "target_field");
        Map<String, Object> data = processor.execute(ingestDocument).getSourceAndMetadata();

        assertThat(data, hasKey("target_field"));
        assertThat(data.get("target_field"), is("1:d/FP5EW3wiY1vCndhwleRRKHowQ="));
    }

    public void testThatProcessorWorksWithIntegerPort() throws Exception {
        Map<String, Object> document = new HashMap<>();
        document.put("source_ip", "192.168.1.52");
        document.put("source_port", 54585);
        document.put("destination_ip", "8.8.8.8");
        document.put("destination_port", 53);
        document.put("transport", "UDP");

        IngestDocument ingestDocument = RandomDocumentPicks.randomIngestDocument(random(), document);

        List<String> field = new ArrayList <String>();
        field.add("source_ip");
        field.add("source_port");
        field.add("destination_ip");
        field.add("destination_port");
        field.add("transport");

        CommunityIdProcessor processor = new CommunityIdProcessor(randomAlphaOfLength(10), field, "target_field");
        Map<String, Object> data = processor.execute(ingestDocument).getSourceAndMetadata();

        assertThat(data, hasKey("target_field"));
        assertThat(data.get("target_field"), is("1:d/FP5EW3wiY1vCndhwleRRKHowQ="));
    }

    public void testGracefullExitWhenAFieldIsNotFound() throws Exception {
        Map<String, Object> document = new HashMap<>();
        document.put("source_ip", "192.168.1.52");
        document.put("source_port", "54585");
        document.put("destination_ip", "8.8.8.8");
        //destination_port port is missing
        document.put("transport", "UDP");

        IngestDocument ingestDocument = RandomDocumentPicks.randomIngestDocument(random(), document);

        List<String> field = new ArrayList <String>();
        field.add("source_ip");
        field.add("source_port");
        field.add("destination_ip");
        field.add("destination_port");
        field.add("transport");

        CommunityIdProcessor processor = new CommunityIdProcessor(randomAlphaOfLength(10), field, "target_field");
        Map<String, Object> data = processor.execute(ingestDocument).getSourceAndMetadata();

        assertThat(data.get("target_field"), is(nullValue()));
    }

    public void testNestedFieldWorks() throws Exception {
        Map<String, Object> document = new HashMap<>();
        Map<String, Object> source = new HashMap<>();
        Map<String, Object> destination = new HashMap<>();

        source.put("ip", "192.168.1.52");
        source.put("port", "54585");
        destination.put("ip", "8.8.8.8");
        destination.put("port", "53");

        document.put("source", source);
        document.put("destination", destination);
        document.put("transport", "UDP");

        IngestDocument ingestDocument = RandomDocumentPicks.randomIngestDocument(random(), document);

        List<String> field = new ArrayList <String>();
        field.add("source.ip");
        field.add("source.port");
        field.add("destination.ip");
        field.add("destination.port");
        field.add("transport");

        CommunityIdProcessor processor = new CommunityIdProcessor(randomAlphaOfLength(10), field, "target_field");
        Map<String, Object> data = processor.execute(ingestDocument).getSourceAndMetadata();

        assertThat(data, hasKey("target_field"));
        assertThat(data.get("target_field"), is("1:d/FP5EW3wiY1vCndhwleRRKHowQ="));
    }

    public void testDeeplyNestedFieldWorks() throws Exception {
        Map<String, Object> document = new HashMap<>();
        Map<String, Object> eventData = new HashMap<>();
        Map<String, Object> source = new HashMap<>();
        Map<String, Object> destination = new HashMap<>();

        source.put("ip", "192.168.1.52");
        source.put("port", "54585");
        destination.put("ip", "8.8.8.8");
        destination.put("port", "53");

        eventData.put("source", source);
        eventData.put("destination", destination);
        eventData.put("Protocol", "UDP");
        document.put("EventData", eventData);

        IngestDocument ingestDocument = RandomDocumentPicks.randomIngestDocument(random(), document);

        List<String> field = new ArrayList <String>();
        field.add("EventData.source.ip");
        field.add("EventData.source.port");
        field.add("EventData.destination.ip");
        field.add("EventData.destination.port");
        field.add("EventData.Protocol");

        CommunityIdProcessor processor = new CommunityIdProcessor(randomAlphaOfLength(10), field, "target_field");
        Map<String, Object> data = processor.execute(ingestDocument).getSourceAndMetadata();

        assertThat(data, hasKey("target_field"));
        assertThat(data.get("target_field"), is("1:d/FP5EW3wiY1vCndhwleRRKHowQ="));
    }
}


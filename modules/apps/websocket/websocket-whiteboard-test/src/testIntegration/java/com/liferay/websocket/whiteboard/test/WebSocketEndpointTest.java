/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.websocket.whiteboard.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.websocket.whiteboard.test.encode.client.EncodeWebSocketClient;
import com.liferay.websocket.whiteboard.test.encode.data.Example;
import com.liferay.websocket.whiteboard.test.encode.data.ExampleDecoder;
import com.liferay.websocket.whiteboard.test.encode.data.ExampleEncoder;
import com.liferay.websocket.whiteboard.test.encode.endpoint.EncodeWebSocketEndpoint;
import com.liferay.websocket.whiteboard.test.simple.client.BinaryWebSocketClient;
import com.liferay.websocket.whiteboard.test.simple.client.TextWebSocketClient;
import com.liferay.websocket.whiteboard.test.simple.endpoint.SimpleWebSocketEndpoint;

import jakarta.websocket.ContainerProvider;
import jakarta.websocket.Decoder;
import jakarta.websocket.Encoder;
import jakarta.websocket.Endpoint;
import jakarta.websocket.WebSocketContainer;

import java.net.URI;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Cristina González
 */
@RunWith(Arquillian.class)
public class WebSocketEndpointTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() {
		Bundle bundle = FrameworkUtil.getBundle(WebSocketEndpointTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		Dictionary<String, Object> properties = new Hashtable<>();

		List<Class<? extends Decoder>> decoders = new ArrayList<>();

		decoders.add(ExampleDecoder.class);

		List<Class<? extends Encoder>> encoders = new ArrayList<>();

		properties.put("org.osgi.http.websocket.endpoint.decoders", decoders);

		encoders.add(ExampleEncoder.class);

		properties.put("org.osgi.http.websocket.endpoint.encoders", encoders);

		properties.put(
			"org.osgi.http.websocket.endpoint.path", "/o/websocket/decoder");

		_endpointServiceRegistration = bundleContext.registerService(
			Endpoint.class, new EncodeWebSocketEndpoint(), properties);

		properties = new Hashtable<>();

		properties.put(
			"org.osgi.http.websocket.endpoint.path", "/o/websocket/test");

		_simpleEndpointServiceRegistration = bundleContext.registerService(
			Endpoint.class, new SimpleWebSocketEndpoint(), properties);
	}

	@AfterClass
	public static void tearDownClass() {
		_endpointServiceRegistration.unregister();

		_simpleEndpointServiceRegistration.unregister();
	}

	@Test
	public void testSendBinaryMessageAndReceiveTheSame() throws Exception {
		WebSocketContainer webSocketContainer =
			ContainerProvider.getWebSocketContainer();

		SynchronousQueue<ByteBuffer> synchronousQueue =
			new SynchronousQueue<>();

		BinaryWebSocketClient binaryWebSocketClient = new BinaryWebSocketClient(
			synchronousQueue);

		URI uri = new URI("ws://localhost:8080/o/websocket/test");

		webSocketContainer.connectToServer(binaryWebSocketClient, uri);

		binaryWebSocketClient.sendMessage(ByteBuffer.wrap("echo".getBytes()));

		ByteBuffer byteBuffer = synchronousQueue.poll(1, TimeUnit.HOURS);

		Assert.assertEquals(
			"echo", new String(byteBuffer.array(), Charset.forName("UTF-8")));
	}

	@Test
	public void testSendMessageAndReceiveTheSame() throws Exception {
		WebSocketContainer webSocketContainer =
			ContainerProvider.getWebSocketContainer();

		SynchronousQueue<String> synchronousQueue = new SynchronousQueue<>();

		TextWebSocketClient textWebSocketClient = new TextWebSocketClient(
			synchronousQueue);

		URI uri = new URI("ws://localhost:8080/o/websocket/test");

		webSocketContainer.connectToServer(textWebSocketClient, uri);

		textWebSocketClient.sendMessage("echo");

		Assert.assertEquals("echo", synchronousQueue.poll(1, TimeUnit.HOURS));
	}

	@Test
	public void testSendObjectAndReceiveTheSame() throws Exception {
		WebSocketContainer webSocketContainer =
			ContainerProvider.getWebSocketContainer();

		SynchronousQueue<Example> synchronousQueue = new SynchronousQueue<>();

		EncodeWebSocketClient encodeWebSocketClient = new EncodeWebSocketClient(
			synchronousQueue);

		URI uri = new URI("ws://localhost:8080/o/websocket/decoder");

		webSocketContainer.connectToServer(encodeWebSocketClient, uri);

		encodeWebSocketClient.sendMessage(new Example(2, "echo"));

		Example example = synchronousQueue.poll(1, TimeUnit.HOURS);

		Assert.assertEquals("echo", example.getData());
		Assert.assertEquals(2, example.getNumber());
	}

	private static ServiceRegistration<Endpoint> _endpointServiceRegistration;
	private static ServiceRegistration<Endpoint>
		_simpleEndpointServiceRegistration;

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.petra.io.unsync;

import java.io.IOException;
import java.io.Reader;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Preston Crary
 */
public abstract class BaseReaderTestCase {

	@Test
	public void testBlockRead() throws Exception {
		Reader reader = getReader("abcdefg");

		char[] chars = new char[4];

		Assert.assertEquals(4, reader.read(chars));

		Assert.assertTrue(Arrays.equals("abcd".toCharArray(), chars));

		Assert.assertEquals(3, reader.read(chars));
		Assert.assertEquals('e', chars[0]);
		Assert.assertEquals('f', chars[1]);
		Assert.assertEquals('g', chars[2]);

		Assert.assertEquals(-1, reader.read(chars));
	}

	@Test
	public void testMarkAndReset() throws Exception {
		Reader reader = getReader(_VALUE);

		Assert.assertEquals('a', reader.read());

		reader.mark(-1);

		Assert.assertEquals('b', reader.read());
		Assert.assertEquals('c', reader.read());
		Assert.assertEquals(-1, reader.read());

		reader.reset();

		Assert.assertEquals('b', reader.read());
		Assert.assertEquals('c', reader.read());
		Assert.assertEquals(-1, reader.read());
	}

	@Test
	public void testMarkSupported() {
		Reader reader = getReader(_VALUE);

		Assert.assertTrue(reader.markSupported());
	}

	@Test
	public void testRead() throws Exception {
		Reader reader = getReader(_VALUE);

		Assert.assertEquals('a', reader.read());
		Assert.assertEquals('b', reader.read());
		Assert.assertEquals('c', reader.read());
		Assert.assertEquals(-1, reader.read());
	}

	@Test
	public void testReadNullCharArray() throws IOException {
		Reader reader = getReader(_VALUE);

		try {
			reader.read(null, 0, 1);

			Assert.fail();
		}
		catch (NullPointerException nullPointerException) {
		}
	}

	@Test
	public void testReadOutOfBoundsLength() throws IOException {
		Reader reader = getReader(_VALUE);

		try {
			reader.read(new char[3], 3, 1);

			Assert.fail();
		}
		catch (IndexOutOfBoundsException indexOutOfBoundsException) {
		}
	}

	@Test
	public void testReadOutOfBoundsNegativeLength() throws IOException {
		Reader reader = getReader(_VALUE);

		try {
			reader.read(new char[3], 0, -1);

			Assert.fail();
		}
		catch (IndexOutOfBoundsException indexOutOfBoundsException) {
		}
	}

	@Test
	public void testReadOutOfBoundsNegativeOffset() throws IOException {
		Reader reader = getReader(_VALUE);

		try {
			reader.read(new char[3], -1, 1);

			Assert.fail();
		}
		catch (IndexOutOfBoundsException indexOutOfBoundsException) {
		}
	}

	@Test
	public void testReadOutOfBoundsOffset() throws IOException {
		Reader reader = getReader(_VALUE);

		try {
			reader.read(new char[3], 4, 1);

			Assert.fail();
		}
		catch (IndexOutOfBoundsException indexOutOfBoundsException) {
		}
	}

	@Test
	public void testReadOutOfBoundsOverflow() throws IOException {
		Reader reader = getReader(_VALUE);

		try {
			reader.read(new char[3], 1, Integer.MAX_VALUE);

			Assert.fail();
		}
		catch (IndexOutOfBoundsException indexOutOfBoundsException) {
		}
	}

	@Test
	public void testReadZeroLength() throws IOException {
		Reader reader = getReader(_VALUE);

		Assert.assertEquals(0, reader.read(new char[0], 0, 0));
	}

	@Test
	public void testReady() throws Exception {
		Reader reader = getReader(_VALUE);

		Assert.assertTrue(reader.ready());
	}

	@Test
	public void testSkip() throws Exception {
		Reader reader = getReader("abcdef");

		Assert.assertEquals('a', reader.read());
		Assert.assertEquals(2, reader.skip(2));
		Assert.assertEquals('d', reader.read());
		Assert.assertEquals(2, reader.skip(3));
		Assert.assertEquals(-1, reader.read());
		Assert.assertEquals(0, reader.skip(3));
	}

	@Test
	public void testSkipNegative() throws IOException {
		Reader reader = getReader("abcdef");

		try {
			reader.skip(-1);

			Assert.fail();
		}
		catch (IllegalArgumentException illegalArgumentException) {
		}
	}

	protected abstract Reader getReader(String s);

	protected void testClose(Reader reader, String message) throws IOException {
		try {
			reader.mark(0);

			Assert.fail();
		}
		catch (IOException ioException) {
			Assert.assertEquals(message, ioException.getMessage());
		}

		try {
			reader.read();

			Assert.fail();
		}
		catch (IOException ioException) {
			Assert.assertEquals(message, ioException.getMessage());
		}

		try {
			reader.read(new char[5]);

			Assert.fail();
		}
		catch (IOException ioException) {
			Assert.assertEquals(message, ioException.getMessage());
		}

		try {
			reader.ready();

			Assert.fail();
		}
		catch (IOException ioException) {
			Assert.assertEquals(message, ioException.getMessage());
		}

		try {
			reader.reset();

			Assert.fail();
		}
		catch (IOException ioException) {
			Assert.assertEquals(message, ioException.getMessage());
		}

		try {
			reader.skip(1);

			Assert.fail();
		}
		catch (IOException ioException) {
			Assert.assertEquals(message, ioException.getMessage());
		}

		reader.close();
	}

	private static final String _VALUE = "abc";

}
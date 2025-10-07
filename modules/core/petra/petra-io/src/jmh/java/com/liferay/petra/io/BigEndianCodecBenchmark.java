/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.petra.io;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

/**
 * @author Shuyang Zhou
 */
@BenchmarkMode(Mode.AverageTime)
@Fork(1)
@Measurement(iterations = 2)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
@Warmup(iterations = 1)
public class BigEndianCodecBenchmark {

	@Benchmark
	public char getChar() {
		return BigEndianCodec.getChar(_bytes, _index);
	}

	@Benchmark
	public double getDouble() {
		return BigEndianCodec.getDouble(_bytes, _index);
	}

	@Benchmark
	public float getFloat() {
		return BigEndianCodec.getFloat(_bytes, _index);
	}

	@Benchmark
	public int getInt() {
		return BigEndianCodec.getInt(_bytes, _index);
	}

	@Benchmark
	public long getLong() {
		return BigEndianCodec.getLong(_bytes, _index);
	}

	@Benchmark
	public short getShort() {
		return BigEndianCodec.getShort(_bytes, _index);
	}

	@Benchmark
	public void putChar() throws Exception {
		BigEndianCodec.putChar(_bytes, _index, _char);
	}

	@Benchmark
	public void putDouble() throws Exception {
		BigEndianCodec.putDouble(_bytes, _index, _double);
	}

	@Benchmark
	public void putFloat() throws Exception {
		BigEndianCodec.putFloat(_bytes, _index, _float);
	}

	@Benchmark
	public void putInt() throws Exception {
		BigEndianCodec.putInt(_bytes, _index, _int);
	}

	@Benchmark
	public void putLong() throws Exception {
		BigEndianCodec.putLong(_bytes, _index, _long);
	}

	@Benchmark
	public void putShort() throws Exception {
		BigEndianCodec.putShort(_bytes, _index, _short);
	}

	@Setup
	public void setUp() {
		_bytes = new byte[128];

		Random random = new Random();

		random.nextBytes(_bytes);

		_index = random.nextInt(_bytes.length - 8);

		_char = (char)random.nextInt();
		_double = random.nextDouble();
		_float = random.nextFloat();
		_int = random.nextInt();
		_long = random.nextLong();
		_short = (short)random.nextInt();
	}

	private byte[] _bytes;
	private char _char;
	private double _double;
	private float _float;
	private int _index;
	private int _int;
	private long _long;
	private short _short;

}
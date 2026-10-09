/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.petra.string;

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
 * See LPD-24313.
 *
 * @author Shuyang Zhou
 */
@BenchmarkMode(Mode.AverageTime)
@Fork(
	jvmArgsAppend = {
		"--add-opens=java.base/java.lang.invoke=ALL-UNNAMED",
		"-XX:+IgnoreUnrecognizedVMOptions"
	},
	value = 1
)
@Measurement(iterations = 2)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
@Warmup(iterations = 1)
public class StringBundlerBenchmark {

	@Benchmark
	public String conditionalGroup8Concat() {
		String s = "";

		for (int i = 0; i < _strings.length; i++) {
			if (i == _excludeIndex) {
				continue;
			}

			s = s.concat(_strings[i]);
		}

		return s;
	}

	@Benchmark
	public String conditionalGroup8Plus() {
		String s = "";

		for (int i = 0; i < _strings.length; i++) {
			if (i == _excludeIndex) {
				continue;
			}

			s += _strings[i];
		}

		return s;
	}

	@Benchmark
	public String conditionalGroup8StringBuilder() {
		StringBuilder sb = new StringBuilder(8);

		for (int i = 0; i < _strings.length; i++) {
			if (i == _excludeIndex) {
				continue;
			}

			sb.append(_strings[i]);
		}

		return sb.toString();
	}

	@Benchmark
	public String conditionalGroup8StringBundler() {
		StringBundler sb = new StringBundler(8);

		for (int i = 0; i < _strings.length; i++) {
			if (i == _excludeIndex) {
				continue;
			}

			sb.append(_strings[i]);
		}

		return sb.toString();
	}

	@Benchmark
	public String group2Concat() {
		return _strings[0].concat(_strings[1]);
	}

	@Benchmark
	public String group2Plus() {
		return _strings[0] + _strings[1];
	}

	@Benchmark
	public String group2StringBuilder() {
		StringBuilder sb = new StringBuilder();

		sb.append(_strings[0]);
		sb.append(_strings[1]);

		return sb.toString();
	}

	@Benchmark
	public String group2StringBundler() {
		StringBundler sb = new StringBundler(2);

		sb.append(_strings[0]);
		sb.append(_strings[1]);

		return sb.toString();
	}

	@Benchmark
	public String group3Concat() {
		return _strings[0].concat(
			_strings[1]
		).concat(
			_strings[2]
		);
	}

	@Benchmark
	public String group3Plus() {
		return _strings[0] + _strings[1] + _strings[2];
	}

	@Benchmark
	public String group3StringBuilder() {
		StringBuilder sb = new StringBuilder();

		sb.append(_strings[0]);
		sb.append(_strings[1]);
		sb.append(_strings[2]);

		return sb.toString();
	}

	@Benchmark
	public String group3StringBundler() {
		StringBundler sb = new StringBundler(3);

		sb.append(_strings[0]);
		sb.append(_strings[1]);
		sb.append(_strings[2]);

		return sb.toString();
	}

	@Benchmark
	public String group4Concat() {
		return _strings[0].concat(
			_strings[1]
		).concat(
			_strings[2]
		).concat(
			_strings[3]
		);
	}

	@Benchmark
	public String group4Plus() {
		return _strings[0] + _strings[1] + _strings[2] + _strings[3];
	}

	@Benchmark
	public String group4StringBuilder() {
		StringBuilder sb = new StringBuilder();

		sb.append(_strings[0]);
		sb.append(_strings[1]);
		sb.append(_strings[2]);
		sb.append(_strings[3]);

		return sb.toString();
	}

	@Benchmark
	public String group4StringBundler() {
		StringBundler sb = new StringBundler(4);

		sb.append(_strings[0]);
		sb.append(_strings[1]);
		sb.append(_strings[2]);
		sb.append(_strings[3]);

		return sb.toString();
	}

	@Benchmark
	public String group8Concat() {
		return _strings[0].concat(
			_strings[1]
		).concat(
			_strings[2]
		).concat(
			_strings[3]
		).concat(
			_strings[4]
		).concat(
			_strings[5]
		).concat(
			_strings[6]
		).concat(
			_strings[7]
		);
	}

	@Benchmark
	public String group8Plus() {
		return _strings[0] + _strings[1] + _strings[2] + _strings[3] +
			_strings[4] + _strings[5] + _strings[6] + _strings[7];
	}

	@Benchmark
	public String group8StringBuilder() {
		StringBuilder sb = new StringBuilder();

		sb.append(_strings[0]);
		sb.append(_strings[1]);
		sb.append(_strings[2]);
		sb.append(_strings[3]);
		sb.append(_strings[4]);
		sb.append(_strings[5]);
		sb.append(_strings[6]);
		sb.append(_strings[7]);

		return sb.toString();
	}

	@Benchmark
	public String group8StringBundler() {
		StringBundler sb = new StringBundler(8);

		sb.append(_strings[0]);
		sb.append(_strings[1]);
		sb.append(_strings[2]);
		sb.append(_strings[3]);
		sb.append(_strings[4]);
		sb.append(_strings[5]);
		sb.append(_strings[6]);
		sb.append(_strings[7]);

		return sb.toString();
	}

	@Setup
	public void setUp() {
		_excludeIndex = 5;

		_strings = new String[8];

		for (int i = 0; i < _strings.length; i++) {
			_strings[i] = "someString" + i;
		}
	}

	private int _excludeIndex;
	private String[] _strings;

}
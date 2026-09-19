/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.whip.instrument;

import com.liferay.whip.coveragedata.TouchUtil;

import java.util.Map;
import java.util.Set;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.MethodNode;

/**
 * @author Shuyang Zhou
 */
public class TouchMethodVisitor extends MethodVisitor {

	public TouchMethodVisitor(
		String owner, MethodNode methodNode, MethodVisitor methodVisitor,
		Set<Label> jumpLabels, Map<Label, Integer> lineLabels,
		Map<Label, SwitchHolder> switchLabels) {

		super(Opcodes.ASM9, methodVisitor);

		_owner = owner;
		_jumpLabels = jumpLabels;
		_lineLabels = lineLabels;
		_switchLabels = switchLabels;

		int variableCount = 0;

		if ((Opcodes.ACC_STATIC & methodNode.access) == 0) {
			variableCount++;
		}

		for (Type type : Type.getArgumentTypes(methodNode.desc)) {
			variableCount += type.getSize();
		}

		_variableCount = variableCount;
	}

	@Override
	public void visitCode() {
		_started = true;

		super.visitCode();
	}

	@Override
	public void visitFieldInsn(
		int opcode, String owner, String name, String desc) {

		_touchBranch();

		super.visitFieldInsn(opcode, owner, name, desc);
	}

	@Override
	public void visitIincInsn(int var, int increment) {
		_touchBranch();

		if (var >= _variableCount) {
			var += 2;
		}

		mv.visitIincInsn(var, increment);
	}

	@Override
	public void visitInsn(int opcode) {
		_touchBranch();

		super.visitInsn(opcode);
	}

	@Override
	public void visitIntInsn(int opcode, int operand) {
		_touchBranch();

		super.visitIntInsn(opcode, operand);
	}

	@Override
	public void visitJumpInsn(int opcode, Label label) {
		_touchBranch();

		if ((_currentLine != 0) && (opcode != Opcodes.GOTO) &&
			(opcode != Opcodes.JSR)) {

			mv.visitIntInsn(Opcodes.SIPUSH, _currentLine);
			mv.visitVarInsn(Opcodes.ISTORE, _variableIndex);
			mv.visitIntInsn(Opcodes.SIPUSH, _currentJump);
			mv.visitVarInsn(Opcodes.ISTORE, _variableIndex + 1);

			_lastJump = new JumpHolder(_currentLine, _currentJump++);
		}

		super.visitJumpInsn(opcode, label);
	}

	@Override
	public void visitLabel(Label label) {
		if (_started) {
			_started = false;

			if (!_jumpLabels.isEmpty()) {
				_variableIndex = _variableCount;

				mv.visitInsn(Opcodes.ICONST_0);
				mv.visitVarInsn(Opcodes.ISTORE, _variableIndex);
				mv.visitIntInsn(Opcodes.SIPUSH, -1);
				mv.visitVarInsn(Opcodes.ISTORE, _variableIndex + 1);

				_startLabel = label;
			}
		}

		_endLabel = label;

		super.visitLabel(label);

		if (_jumpLabels.contains(label)) {
			if (_lastJump != null) {
				Label label1 = _lastJump();

				_touchJump(false);

				Label label2 = new Label();

				mv.visitJumpInsn(Opcodes.GOTO, label2);

				mv.visitLabel(label1);
				mv.visitVarInsn(Opcodes.ILOAD, _variableIndex + 1);
				mv.visitJumpInsn(Opcodes.IFLT, label2);

				_touchJump(true);

				mv.visitLabel(label2);
			}
			else {
				mv.visitVarInsn(Opcodes.ILOAD, _variableIndex + 1);

				Label label1 = new Label();

				mv.visitJumpInsn(Opcodes.IFLT, label1);

				_touchJump(true);

				mv.visitLabel(label1);
			}
		}
		else if (_lastJump != null) {
			Label label1 = _lastJump();

			_touchJump(false);

			mv.visitLabel(label1);
		}

		_lastJump = null;

		SwitchHolder switchHolder = _switchLabels.get(label);

		if (switchHolder != null) {
			_touchSwitch(
				switchHolder.getLineNumber(), switchHolder.getJumpNumber(),
				switchHolder.getBranch());
		}

		Integer line = _lineLabels.get(label);

		if (line != null) {
			visitLineNumber(line, label);
		}
	}

	@Override
	public void visitLdcInsn(Object cst) {
		_touchBranch();

		super.visitLdcInsn(cst);
	}

	@Override
	public void visitLineNumber(int line, Label start) {
		_currentLine = line;
		_currentJump = 0;

		mv.visitLdcInsn(_owner);
		mv.visitIntInsn(Opcodes.SIPUSH, line);
		mv.visitMethodInsn(
			Opcodes.INVOKESTATIC, _TOUCH_UTIL_CLASS, "touch",
			"(Ljava/lang/String;I)V", false);

		super.visitLineNumber(line, start);
	}

	@Override
	public void visitLocalVariable(
		String name, String desc, String signature, Label start, Label end,
		int index) {

		if (index >= _variableCount) {
			index += 2;
		}

		mv.visitLocalVariable(name, desc, signature, start, end, index);
	}

	@Override
	public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
		_touchBranch();

		super.visitLookupSwitchInsn(dflt, keys, labels);
	}

	@Override
	public void visitMaxs(int maxStack, int maxLocals) {
		if (!_jumpLabels.isEmpty()) {
			mv.visitLocalVariable(
				"__whip__line__number__", "I", null, _startLabel, _endLabel,
				_variableIndex);

			mv.visitLocalVariable(
				"__whip__branch__number__", "I", null, _startLabel, _endLabel,
				_variableIndex + 1);
		}

		super.visitMaxs(maxStack, maxLocals);
	}

	@Override
	public void visitMethodInsn(
		int opcode, String owner, String name, String desc) {

		visitMethodInsn(opcode, owner, name, desc, false);
	}

	@Override
	public void visitMethodInsn(
		int opcode, String owner, String name, String desc, boolean itf) {

		_touchBranch();

		super.visitMethodInsn(opcode, owner, name, desc, itf);
	}

	@Override
	public void visitMultiANewArrayInsn(String desc, int dims) {
		_touchBranch();

		super.visitMultiANewArrayInsn(desc, dims);
	}

	@Override
	public void visitTableSwitchInsn(
		int min, int max, Label dflt, Label... labels) {

		_touchBranch();

		super.visitTableSwitchInsn(min, max, dflt, labels);
	}

	@Override
	public void visitTryCatchBlock(
		Label start, Label end, Label handler, String type) {

		_touchBranch();

		super.visitTryCatchBlock(start, end, handler, type);
	}

	@Override
	public void visitTypeInsn(int opcode, String desc) {
		_touchBranch();

		super.visitTypeInsn(opcode, desc);
	}

	@Override
	public void visitVarInsn(int opcode, int var) {
		_touchBranch();

		if (var >= _variableCount) {
			var += 2;
		}

		mv.visitVarInsn(opcode, var);
	}

	private Label _lastJump() {
		mv.visitVarInsn(Opcodes.ILOAD, _variableIndex);
		mv.visitIntInsn(Opcodes.SIPUSH, _lastJump.getLineNumber());

		Label label = new Label();

		mv.visitJumpInsn(Opcodes.IF_ICMPNE, label);

		mv.visitVarInsn(Opcodes.ILOAD, _variableIndex + 1);
		mv.visitIntInsn(Opcodes.SIPUSH, _lastJump.getJumpNumber());
		mv.visitJumpInsn(Opcodes.IF_ICMPNE, label);

		return label;
	}

	private void _touchBranch() {
		if (_lastJump != null) {
			_lastJump = null;

			_touchJump(false);
		}
	}

	private void _touchJump(boolean branch) {
		mv.visitLdcInsn(_owner);

		mv.visitVarInsn(Opcodes.ILOAD, _variableIndex);
		mv.visitVarInsn(Opcodes.ILOAD, _variableIndex + 1);

		if (branch) {
			mv.visitInsn(Opcodes.ICONST_0);
		}
		else {
			mv.visitInsn(Opcodes.ICONST_1);
		}

		mv.visitMethodInsn(
			Opcodes.INVOKESTATIC, _TOUCH_UTIL_CLASS, "touchJump",
			"(Ljava/lang/String;IIZ)V", false);

		mv.visitIntInsn(Opcodes.SIPUSH, -1);
		mv.visitVarInsn(Opcodes.ISTORE, _variableIndex + 1);
	}

	private void _touchSwitch(int lineNumber, int switchNumber, int branch) {
		mv.visitLdcInsn(_owner);

		mv.visitIntInsn(Opcodes.SIPUSH, lineNumber);
		mv.visitIntInsn(Opcodes.SIPUSH, switchNumber);
		mv.visitIntInsn(Opcodes.SIPUSH, branch);

		mv.visitMethodInsn(
			Opcodes.INVOKESTATIC, _TOUCH_UTIL_CLASS, "touchSwitch",
			"(Ljava/lang/String;III)V", false);
	}

	private static final String _TOUCH_UTIL_CLASS = Type.getInternalName(
		TouchUtil.class);

	private int _currentJump;
	private int _currentLine;
	private Label _endLabel;
	private final Set<Label> _jumpLabels;
	private JumpHolder _lastJump;
	private final Map<Label, Integer> _lineLabels;
	private final String _owner;
	private Label _startLabel;
	private boolean _started;
	private final Map<Label, SwitchHolder> _switchLabels;
	private final int _variableCount;
	private int _variableIndex;

}
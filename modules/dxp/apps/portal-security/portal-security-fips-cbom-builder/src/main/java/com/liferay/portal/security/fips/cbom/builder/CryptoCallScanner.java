/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.fips.cbom.builder;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.crypto.Cipher;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.cyclonedx.model.component.crypto.enums.CryptoFunction;
import org.cyclonedx.model.component.crypto.enums.RelatedCryptoMaterialType;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Handle;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.Frame;
import org.objectweb.asm.tree.analysis.SourceInterpreter;
import org.objectweb.asm.tree.analysis.SourceValue;

/**
 * @author Manuele Castro
 */
public class CryptoCallScanner {

	public CryptoCallScanner(boolean fipsEnabled) {
		this(null, fipsEnabled);
	}

	public CryptoCallScanner(
		Set<String> bundleSymbolicNames, boolean fipsEnabled) {

		_bundleSymbolicNames = bundleSymbolicNames;
		_fipsEnabled = fipsEnabled;
	}

	public List<CryptoCall> getCryptoCalls() {
		List<CryptoCall> cryptoCalls = new ArrayList<>();

		for (ScannedCryptoCall scannedCryptoCall : _scannedCryptoCalls) {
			cryptoCalls.addAll(_toCryptoCalls(scannedCryptoCall));
		}

		return cryptoCalls;
	}

	public void scan(File file) throws IOException {
		String fileName = file.getName();

		if (fileName.endsWith(".lpkg")) {
			try (ZipFile zipFile = new ZipFile(file)) {
				for (ZipEntry zipEntry : Collections.list(zipFile.entries())) {
					String name = zipEntry.getName();

					if (!name.endsWith(".jar")) {
						continue;
					}

					try (JarInputStream jarInputStream = new JarInputStream(
							zipFile.getInputStream(zipEntry))) {

						_scan(jarInputStream, name);
					}
				}
			}

			return;
		}

		try (JarInputStream jarInputStream = new JarInputStream(
				new FileInputStream(file))) {

			_scan(jarInputStream, fileName);
		}
	}

	public void scan(
			String bundleSymbolicName, String featureName,
			InputStream inputStream)
		throws IOException {

		if (!_isScanned(bundleSymbolicName)) {
			return;
		}

		byte[] bytes = inputStream.readAllBytes();

		ClassNode classNode = new ClassNode();

		ClassReader classReader = new ClassReader(bytes);

		classReader.accept(
			classNode, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);

		_scan(classNode, bundleSymbolicName, featureName);
	}

	private String _addCallReference(
		List<Origin> argumentOrigins, String methodKey) {

		String reference = _CALL_REFERENCE_PREFIX + _callReferenceCount++;

		_callSites.put(reference, new CallSite(argumentOrigins, methodKey));

		return reference;
	}

	private String _addComputedReference(Origin origin) {
		String reference =
			_COMPUTED_REFERENCE_PREFIX + _computedReferenceCount++;

		_referenceOrigins.put(reference, origin);

		return reference;
	}

	private void _addImplementationMethodKey(
		String methodKey, Set<String> methodKeys) {

		Integer access = _methodAccesses.get(methodKey);

		if ((access != null) && ((access & Opcodes.ACC_ABSTRACT) == 0)) {
			methodKeys.add(methodKey);
		}
	}

	private void _addSpringBean(
		Map<Integer, String> arguments, String className) {

		if (arguments.isEmpty() || Validator.isNull(className)) {
			return;
		}

		List<List<String>> argumentsList =
			_springConstructorArguments.computeIfAbsent(
				className, key -> new ArrayList<>());

		argumentsList.add(new ArrayList<>(arguments.values()));
	}

	private Frame<SourceValue>[] _analyze(
			ClassNode classNode, MethodNode methodNode)
		throws AnalyzerException {

		_foldFIPSEnabled(methodNode.instructions);

		Analyzer<SourceValue> analyzer = new Analyzer<>(
			new OriginInterpreter(
				_getMethodKey(classNode.name, methodNode.name, methodNode.desc),
				methodNode));

		return analyzer.analyze(classNode.name, methodNode);
	}

	private void _foldFIPSEnabled(InsnList insnList) {
		for (AbstractInsnNode abstractInsnNode : insnList.toArray()) {
			if (!_isFIPSEnabled(abstractInsnNode)) {
				continue;
			}

			AbstractInsnNode nextAbstractInsnNode = abstractInsnNode.getNext();

			while ((nextAbstractInsnNode != null) &&
				   (nextAbstractInsnNode.getOpcode() == -1)) {

				nextAbstractInsnNode = nextAbstractInsnNode.getNext();
			}

			if ((nextAbstractInsnNode == null) ||
				((nextAbstractInsnNode.getOpcode() != Opcodes.IFEQ) &&
				 (nextAbstractInsnNode.getOpcode() != Opcodes.IFNE))) {

				continue;
			}

			JumpInsnNode jumpInsnNode = (JumpInsnNode)nextAbstractInsnNode;

			insnList.remove(abstractInsnNode);

			if ((jumpInsnNode.getOpcode() == Opcodes.IFEQ) != _fipsEnabled) {
				insnList.set(
					jumpInsnNode,
					new JumpInsnNode(Opcodes.GOTO, jumpInsnNode.label));
			}
			else {
				insnList.remove(jumpInsnNode);
			}
		}
	}

	private Origin _getAlgorithmOrigin(
		Frame<SourceValue>[] frames, InsnList insnList,
		MethodInsnNode methodInsnNode) {

		Type[] argumentTypes = Type.getArgumentTypes(methodInsnNode.desc);

		if (Objects.equals(methodInsnNode.owner, _CLASS_NAME_DIGESTER_UTIL) &&
			((argumentTypes.length < 2) ||
			 !Objects.equals(argumentTypes[0], Type.getType(String.class)))) {

			Origin origin = new Origin();

			origin.addReference(
				_addCallReference(
					Collections.emptyList(),
					_getMethodKey(
						_CLASS_NAME_DIGESTER_UTIL, "_getDefaultAlgorithm",
						"()Ljava/lang/String;")));

			return origin;
		}

		String fixedAlgorithm = _getFixedAlgorithm(methodInsnNode);

		if (fixedAlgorithm != null) {
			Origin origin = new Origin();

			origin.addValue(fixedAlgorithm);

			return origin;
		}

		int index = 0;

		if (Objects.equals(methodInsnNode.owner, _CLASS_NAME_SECRET_KEY_SPEC)) {
			index = argumentTypes.length - 1;
		}

		return _getArgumentOrigin(
			frames, insnList, methodInsnNode, index, argumentTypes.length);
	}

	private SourceValue _getArgument(
		Frame<SourceValue> frame, int index, int count) {

		return frame.getStack(frame.getStackSize() - count + index);
	}

	private Origin _getArgumentOrigin(
		Frame<SourceValue>[] frames, InsnList insnList,
		AbstractInsnNode abstractInsnNode, int index, int count) {

		return _getOrigin(
			frames, insnList,
			_getArgument(
				frames[insnList.indexOf(abstractInsnNode)], index, count),
			new HashSet<>());
	}

	private ResolutionContext _getBoundResolutionContext(
		String methodKey, ResolutionContext resolutionContext) {

		while (resolutionContext != null) {
			if (Objects.equals(resolutionContext.getMethodKey(), methodKey)) {
				return resolutionContext;
			}

			resolutionContext = resolutionContext.getParentResolutionContext();
		}

		return null;
	}

	private String _getBundleSymbolicName(String fileName, Manifest manifest) {
		if (manifest != null) {
			Attributes attributes = manifest.getMainAttributes();

			String bundleSymbolicName = attributes.getValue(
				"Bundle-SymbolicName");

			if (Validator.isNotNull(bundleSymbolicName)) {
				int index = bundleSymbolicName.indexOf(CharPool.SEMICOLON);

				if (index != -1) {
					bundleSymbolicName = bundleSymbolicName.substring(0, index);
				}

				return StringUtil.trim(bundleSymbolicName);
			}
		}

		return _getFileName(fileName);
	}

	private Set<String> _getCallerMethodKeys(String methodKey) {
		int index = methodKey.lastIndexOf(
			CharPool.PERIOD, methodKey.indexOf(CharPool.OPEN_PARENTHESIS));

		String signature = methodKey.substring(index);

		Integer access = _methodAccesses.get(methodKey);

		if ((access == null) || signature.startsWith(".<init>(") ||
			((access & (Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC)) != 0)) {

			return Collections.singleton(methodKey);
		}

		String className = methodKey.substring(0, index);

		Set<String> methodKeys = new TreeSet<>();

		for (String relatedClassName : _getSubtypeNames(className)) {
			methodKeys.add(relatedClassName + signature);
		}

		for (String relatedClassName : _getSupertypeNames(className)) {
			methodKeys.add(relatedClassName + signature);
		}

		return methodKeys;
	}

	private String _getClassName(String internalName) {
		String className = StringUtil.replace(
			internalName, CharPool.SLASH, CharPool.PERIOD);

		int index = className.indexOf(CharPool.DOLLAR);

		if (index != -1) {
			className = className.substring(0, index);
		}

		return className;
	}

	private List<MethodInsnNode> _getConstructorMethodInsnNodes(
		Frame<SourceValue>[] frames, InsnList insnList,
		TypeInsnNode typeInsnNode) {

		List<MethodInsnNode> methodInsnNodes = new ArrayList<>();

		for (AbstractInsnNode abstractInsnNode : insnList) {
			if (abstractInsnNode.getOpcode() != Opcodes.INVOKESPECIAL) {
				continue;
			}

			MethodInsnNode methodInsnNode = (MethodInsnNode)abstractInsnNode;

			Frame<SourceValue> frame = frames[insnList.indexOf(methodInsnNode)];

			if ((frame == null) ||
				!Objects.equals(methodInsnNode.owner, typeInsnNode.desc) ||
				!Objects.equals(methodInsnNode.name, "<init>")) {

				continue;
			}

			SourceValue sourceValue = _getReceiver(
				frame, Type.getArgumentCount(methodInsnNode.desc));

			if (!sourceValue.insns.contains(typeInsnNode)) {
				continue;
			}

			methodInsnNodes.add(methodInsnNode);
		}

		return methodInsnNodes;
	}

	private List<CryptoFunction> _getCryptoFunctions(
		Frame<SourceValue>[] frames, InsnList insnList, String jcaClassName,
		MethodInsnNode methodInsnNode) {

		CryptoFunction cryptoFunction = _cryptoFunctions.get(jcaClassName);

		if (cryptoFunction != null) {
			return Collections.singletonList(cryptoFunction);
		}

		Set<CryptoFunction> cryptoFunctions = new TreeSet<>();

		for (MethodInsnNode linkedMethodInsnNode :
				_getLinkedMethodInsnNodes(frames, insnList, methodInsnNode)) {

			if (Objects.equals(linkedMethodInsnNode.name, "initSign")) {
				cryptoFunctions.add(CryptoFunction.SIGN);
			}
			else if (Objects.equals(linkedMethodInsnNode.name, "initVerify")) {
				cryptoFunctions.add(CryptoFunction.VERIFY);
			}
			else if (Objects.equals(linkedMethodInsnNode.name, "init") &&
					 linkedMethodInsnNode.desc.startsWith("(I")) {

				Origin origin = _getArgumentOrigin(
					frames, insnList, linkedMethodInsnNode, 0,
					Type.getArgumentCount(linkedMethodInsnNode.desc));

				for (String value : origin.getValues()) {
					cryptoFunction = _cipherCryptoFunctions.get(value);

					if (cryptoFunction == null) {
						continue;
					}

					cryptoFunctions.add(cryptoFunction);
				}
			}
		}

		if (cryptoFunctions.isEmpty()) {
			return Collections.singletonList(CryptoFunction.UNKNOWN);
		}

		return new ArrayList<>(cryptoFunctions);
	}

	private List<MethodInsnNode> _getCryptoMethodInsnNodes(
		ClassNode classNode, MethodNode methodNode) {

		List<MethodInsnNode> methodInsnNodes = new ArrayList<>();

		if (Objects.equals(classNode.name, _CLASS_NAME_DIGESTER_UTIL) ||
			Objects.equals(classNode.name, _CLASS_NAME_LIFERAY_SECURE_RANDOM) ||
			Objects.equals(classNode.name, _CLASS_NAME_SECURE_RANDOM_UTIL)) {

			return methodInsnNodes;
		}

		for (AbstractInsnNode abstractInsnNode : methodNode.instructions) {
			if (!(abstractInsnNode instanceof MethodInsnNode)) {
				continue;
			}

			MethodInsnNode methodInsnNode = (MethodInsnNode)abstractInsnNode;

			if (_getJCAClassName(methodInsnNode) == null) {
				continue;
			}

			methodInsnNodes.add(methodInsnNode);
		}

		return methodInsnNodes;
	}

	private String _getFeatureName(String fileName, Manifest manifest) {
		if (manifest != null) {
			Attributes attributes = manifest.getMainAttributes();

			String bundleName = attributes.getValue("Bundle-Name");

			if (Validator.isNotNull(bundleName)) {
				return bundleName;
			}
		}

		return _getFileName(fileName);
	}

	private String _getFieldKey(FieldInsnNode fieldInsnNode) {
		return _getKeyName(fieldInsnNode.owner) + StringPool.PERIOD +
			fieldInsnNode.name;
	}

	private String _getFileName(String fileName) {
		String name = fileName.substring(
			fileName.lastIndexOf(CharPool.SLASH) + 1);

		int index = name.lastIndexOf(CharPool.PERIOD);

		if (index > 0) {
			name = name.substring(0, index);
		}

		return name;
	}

	private String _getFixedAlgorithm(MethodInsnNode methodInsnNode) {
		String key =
			methodInsnNode.owner + StringPool.PERIOD + methodInsnNode.name;

		if (key.equals("java/security/SecureRandom.<init>") ||
			key.equals(_CLASS_NAME_LIFERAY_SECURE_RANDOM + ".<init>") ||
			(Objects.equals(
				methodInsnNode.owner, _CLASS_NAME_SECURE_RANDOM_UTIL) &&
			 methodInsnNode.name.startsWith("next"))) {

			return _DEFAULT_SECURE_RANDOM_ALGORITHM;
		}

		return _fixedAlgorithms.get(key);
	}

	private Set<String> _getImplementationMethodKeys(String methodKey) {
		int index = methodKey.lastIndexOf(
			CharPool.PERIOD, methodKey.indexOf(CharPool.OPEN_PARENTHESIS));

		String signature = methodKey.substring(index);

		Integer access = _methodAccesses.get(methodKey);

		if ((access != null) &&
			(signature.startsWith(".<init>(") ||
			 ((access & (Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC)) != 0))) {

			return Collections.singleton(methodKey);
		}

		String className = methodKey.substring(0, index);

		Set<String> methodKeys = new TreeSet<>();

		for (String subtypeName : _getSubtypeNames(className)) {
			_addImplementationMethodKey(subtypeName + signature, methodKeys);
		}

		String superClassName = _superClassNames.get(className);

		while (superClassName != null) {
			String superMethodKey = superClassName + signature;

			if (_methodAccesses.containsKey(superMethodKey)) {
				_addImplementationMethodKey(superMethodKey, methodKeys);

				break;
			}

			superClassName = _superClassNames.get(superClassName);
		}

		return methodKeys;
	}

	private String _getJCAClassName(MethodInsnNode methodInsnNode) {
		String fixedAlgorithmClassName = _fixedAlgorithmClassNames.get(
			methodInsnNode.owner + StringPool.PERIOD + methodInsnNode.name);

		if (fixedAlgorithmClassName != null) {
			return fixedAlgorithmClassName;
		}

		if ((methodInsnNode.getOpcode() == Opcodes.INVOKESTATIC) &&
			Objects.equals(
				methodInsnNode.owner, _CLASS_NAME_SECURE_RANDOM_UTIL) &&
			methodInsnNode.name.startsWith("next")) {

			return "SecureRandom";
		}

		if (methodInsnNode.getOpcode() == Opcodes.INVOKESTATIC) {
			if (Objects.equals(
					methodInsnNode.owner, _CLASS_NAME_DIGESTER_UTIL) &&
				methodInsnNode.name.startsWith("digest")) {

				return "MessageDigest";
			}

			if (Objects.equals(methodInsnNode.name, "getInstance") &&
				methodInsnNode.desc.startsWith("(Ljava/lang/String;")) {

				return _jcaClassNames.get(methodInsnNode.owner);
			}

			return null;
		}

		if ((methodInsnNode.getOpcode() == Opcodes.INVOKESPECIAL) &&
			Objects.equals(methodInsnNode.owner, _CLASS_NAME_SECRET_KEY_SPEC) &&
			Objects.equals(methodInsnNode.name, "<init>") &&
			methodInsnNode.desc.endsWith("Ljava/lang/String;)V")) {

			return "SecretKeySpec";
		}

		return null;
	}

	private String _getKeyName(String internalName) {
		return StringUtil.replace(
			internalName, CharPool.SLASH, CharPool.PERIOD);
	}

	private Origin _getKeySizeOrigin(
		Frame<SourceValue>[] frames, InsnList insnList,
		MethodInsnNode methodInsnNode) {

		Origin origin = new Origin();

		String methodName = _keySizeMethodNames.get(methodInsnNode.owner);

		if (methodName == null) {
			return origin;
		}

		for (MethodInsnNode linkedMethodInsnNode :
				_getLinkedMethodInsnNodes(frames, insnList, methodInsnNode)) {

			if (!Objects.equals(linkedMethodInsnNode.name, methodName) ||
				!linkedMethodInsnNode.desc.startsWith("(I")) {

				continue;
			}

			origin.addAll(
				_getArgumentOrigin(
					frames, insnList, linkedMethodInsnNode, 0,
					Type.getArgumentCount(linkedMethodInsnNode.desc)));
		}

		return origin;
	}

	private List<MethodInsnNode> _getLinkedMethodInsnNodes(
		Frame<SourceValue>[] frames, InsnList insnList,
		MethodInsnNode methodInsnNode) {

		List<MethodInsnNode> linkedMethodInsnNodes = new ArrayList<>();

		for (AbstractInsnNode abstractInsnNode : insnList) {
			if (abstractInsnNode.getOpcode() != Opcodes.INVOKEVIRTUAL) {
				continue;
			}

			MethodInsnNode curMethodInsnNode = (MethodInsnNode)abstractInsnNode;

			Frame<SourceValue> frame =
				frames[insnList.indexOf(curMethodInsnNode)];

			if ((frame == null) ||
				!Objects.equals(
					curMethodInsnNode.owner, methodInsnNode.owner)) {

				continue;
			}

			SourceValue sourceValue = _getReceiver(
				frame, Type.getArgumentCount(curMethodInsnNode.desc));

			if (!sourceValue.insns.contains(methodInsnNode)) {
				continue;
			}

			linkedMethodInsnNodes.add(curMethodInsnNode);
		}

		return linkedMethodInsnNodes;
	}

	private String _getMethodKey(
		String internalName, String name, String desc) {

		return StringBundler.concat(
			_getKeyName(internalName), StringPool.PERIOD, name, desc);
	}

	private Origin _getMethodOrigin(
		ClassNode classNode, MethodNode methodNode) {

		Origin origin = new Origin();

		String methodKey = _getMethodKey(
			classNode.name, methodNode.name, methodNode.desc);

		Type[] argumentTypes = Type.getArgumentTypes(methodNode.desc);

		for (int i = 0; i < argumentTypes.length; i++) {
			if (!Objects.equals(argumentTypes[i], Type.getType(String.class))) {
				continue;
			}

			origin.addReference(methodKey + StringPool.POUND + i);
		}

		return origin;
	}

	private Origin _getOrigin(
		Frame<SourceValue>[] frames, InsnList insnList,
		AbstractInsnNode abstractInsnNode,
		Set<AbstractInsnNode> visitedAbstractInsnNodes) {

		Origin origin = new Origin();

		int opcode = abstractInsnNode.getOpcode();

		if (abstractInsnNode instanceof ParameterInsnNode) {
			ParameterInsnNode parameterInsnNode =
				(ParameterInsnNode)abstractInsnNode;

			origin.addReference(parameterInsnNode.getParameterKey());
		}
		else if (abstractInsnNode instanceof LdcInsnNode) {
			LdcInsnNode ldcInsnNode = (LdcInsnNode)abstractInsnNode;

			origin.addValue(String.valueOf(ldcInsnNode.cst));
		}
		else if ((opcode >= Opcodes.ICONST_M1) &&
				 (opcode <= Opcodes.ICONST_5)) {

			origin.addValue(String.valueOf(opcode - Opcodes.ICONST_0));
		}
		else if ((opcode == Opcodes.BIPUSH) || (opcode == Opcodes.SIPUSH)) {
			IntInsnNode intInsnNode = (IntInsnNode)abstractInsnNode;

			origin.addValue(String.valueOf(intInsnNode.operand));
		}
		else if ((opcode == Opcodes.GETFIELD) ||
				 (opcode == Opcodes.GETSTATIC)) {

			origin.addReference(_getFieldKey((FieldInsnNode)abstractInsnNode));
		}
		else if (opcode == Opcodes.CHECKCAST) {
			origin.addAll(
				_getOrigin(
					frames, insnList,
					_getArgument(
						frames[insnList.indexOf(abstractInsnNode)], 0, 1),
					visitedAbstractInsnNodes));
		}
		else if (abstractInsnNode instanceof MethodInsnNode) {
			origin.addAll(
				_getReturnValueOrigin(
					frames, insnList, (MethodInsnNode)abstractInsnNode,
					visitedAbstractInsnNodes));
		}
		else if ((opcode == Opcodes.NEW) &&
				 _isSecretKeySpec((TypeInsnNode)abstractInsnNode)) {

			for (MethodInsnNode methodInsnNode :
					_getConstructorMethodInsnNodes(
						frames, insnList, (TypeInsnNode)abstractInsnNode)) {

				origin.addAll(
					_getAlgorithmOrigin(frames, insnList, methodInsnNode));
			}
		}
		else if ((abstractInsnNode instanceof InvokeDynamicInsnNode) &&
				 _isBootstrappedBy(
					 (InvokeDynamicInsnNode)abstractInsnNode,
					 "java/lang/invoke/StringConcatFactory")) {

			InvokeDynamicInsnNode invokeDynamicInsnNode =
				(InvokeDynamicInsnNode)abstractInsnNode;

			Frame<SourceValue> frame =
				frames[insnList.indexOf(abstractInsnNode)];
			Type[] argumentTypes = Type.getArgumentTypes(
				invokeDynamicInsnNode.desc);

			Origin computedOrigin = new Origin();

			for (int i = 0; i < argumentTypes.length; i++) {
				if (!Objects.equals(
						argumentTypes[i], Type.getType(String.class))) {

					continue;
				}

				computedOrigin.addAll(
					_getOrigin(
						frames, insnList,
						_getArgument(frame, i, argumentTypes.length),
						visitedAbstractInsnNodes));
			}

			origin.addReference(_addComputedReference(computedOrigin));
		}
		else if (opcode != Opcodes.ACONST_NULL) {
			origin.addUnresolvedReason("a value the code computes");
		}

		return origin;
	}

	private Origin _getOrigin(
		Frame<SourceValue>[] frames, InsnList insnList, SourceValue sourceValue,
		Set<AbstractInsnNode> visitedAbstractInsnNodes) {

		Origin origin = new Origin();

		for (AbstractInsnNode abstractInsnNode : sourceValue.insns) {
			if (!visitedAbstractInsnNodes.add(abstractInsnNode)) {
				continue;
			}

			origin.addAll(
				_getOrigin(
					frames, insnList, abstractInsnNode,
					visitedAbstractInsnNodes));
		}

		return origin;
	}

	private Origin _getParameterOrigin(String methodKey, String index) {
		Origin origin = null;

		for (String callerMethodKey : _getCallerMethodKeys(methodKey)) {
			Origin referenceOrigin = _referenceOrigins.get(
				callerMethodKey + StringPool.POUND + index);

			if (referenceOrigin == null) {
				continue;
			}

			if (origin == null) {
				origin = new Origin();
			}

			origin.addAll(referenceOrigin);
		}

		Origin springOrigin = _getSpringOrigin(
			methodKey + StringPool.POUND + index);

		if (springOrigin == null) {
			return origin;
		}

		if (origin != null) {
			springOrigin.addAll(origin);
		}

		return springOrigin;
	}

	private SourceValue _getReceiver(Frame<SourceValue> frame, int count) {
		return frame.getStack(frame.getStackSize() - count - 1);
	}

	private Origin _getReferenceOrigin(String reference) {
		int index = reference.indexOf(CharPool.POUND);

		if (index != -1) {
			return _getParameterOrigin(
				reference.substring(0, index), reference.substring(index + 1));
		}

		return _referenceOrigins.get(reference);
	}

	private List<RelatedCryptoMaterialType> _getRelatedCryptoMaterialTypes(
		Frame<SourceValue>[] frames, InsnList insnList, String jcaClassName,
		MethodInsnNode methodInsnNode) {

		if (!Objects.equals(jcaClassName, "KeyFactory")) {
			return Collections.emptyList();
		}

		Set<RelatedCryptoMaterialType> relatedCryptoMaterialTypes =
			new TreeSet<>();

		for (MethodInsnNode linkedMethodInsnNode :
				_getLinkedMethodInsnNodes(frames, insnList, methodInsnNode)) {

			RelatedCryptoMaterialType relatedCryptoMaterialType =
				_keyFactoryRelatedCryptoMaterialTypes.get(
					linkedMethodInsnNode.name);

			if (relatedCryptoMaterialType == null) {
				continue;
			}

			relatedCryptoMaterialTypes.add(relatedCryptoMaterialType);
		}

		return new ArrayList<>(relatedCryptoMaterialTypes);
	}

	private Origin _getReturnValueOrigin(
		Frame<SourceValue>[] frames, InsnList insnList,
		MethodInsnNode methodInsnNode,
		Set<AbstractInsnNode> visitedAbstractInsnNodes) {

		Origin origin = new Origin();

		String propertyKey = _jdkPropertyKeys.get(
			methodInsnNode.owner + StringPool.PERIOD + methodInsnNode.name);

		if (propertyKey != null) {
			origin.addPropertyKey(propertyKey);

			return origin;
		}

		Frame<SourceValue> frame = frames[insnList.indexOf(methodInsnNode)];
		int count = Type.getArgumentCount(methodInsnNode.desc);

		if (methodInsnNode.owner.endsWith("/PropsUtil") &&
			Objects.equals(methodInsnNode.name, "get") && (count > 0)) {

			Origin keyOrigin = _getOrigin(
				frames, insnList, _getArgument(frame, 0, count),
				visitedAbstractInsnNodes);

			for (String value : keyOrigin.getValues()) {
				origin.addPropertyKey(value);
			}

			if (!keyOrigin.isConstant()) {
				origin.addUnresolvedReason(
					"a property whose name the code computes");
			}

			return origin;
		}

		if (_keyClassNames.contains(methodInsnNode.owner) &&
			Objects.equals(methodInsnNode.name, "getAlgorithm") &&
			(count == 0)) {

			return _getOrigin(
				frames, insnList, _getReceiver(frame, count),
				visitedAbstractInsnNodes);
		}

		if (_jcaClassNames.containsKey(methodInsnNode.owner) &&
			_keyMethodNames.contains(methodInsnNode.name)) {

			for (AbstractInsnNode abstractInsnNode :
					_getReceiver(frame, count).insns) {

				if ((abstractInsnNode instanceof MethodInsnNode) &&
					(_getJCAClassName((MethodInsnNode)abstractInsnNode) !=
						null)) {

					origin.addAll(
						_getAlgorithmOrigin(
							frames, insnList,
							(MethodInsnNode)abstractInsnNode));
				}
				else {
					origin.addUnresolvedReason(
						"the algorithm of a " +
							_jcaClassNames.get(methodInsnNode.owner) +
								" that the code does not create");
				}
			}

			return origin;
		}

		Function<String, String> function = _stringFunctions.get(
			methodInsnNode.owner + StringPool.PERIOD + methodInsnNode.name);

		if ((function == null) &&
			_computingClassNames.contains(methodInsnNode.owner) &&
			Objects.equals(
				Type.getReturnType(methodInsnNode.desc),
				Type.getType(String.class))) {

			Origin computedOrigin = new Origin();

			if ((methodInsnNode.getOpcode() != Opcodes.INVOKESTATIC) &&
				Objects.equals(methodInsnNode.owner, "java/lang/String")) {

				computedOrigin.addAll(
					_getOrigin(
						frames, insnList, _getReceiver(frame, count),
						visitedAbstractInsnNodes));
			}

			Type[] argumentTypes = Type.getArgumentTypes(methodInsnNode.desc);

			for (int i = 0; i < argumentTypes.length; i++) {
				if (!Objects.equals(
						argumentTypes[i], Type.getType(String.class))) {

					continue;
				}

				computedOrigin.addAll(
					_getOrigin(
						frames, insnList, _getArgument(frame, i, count),
						visitedAbstractInsnNodes));
			}

			origin.addReference(_addComputedReference(computedOrigin));

			return origin;
		}

		if (function == null) {
			List<Origin> argumentOrigins = new ArrayList<>();

			Type[] argumentTypes = Type.getArgumentTypes(methodInsnNode.desc);

			for (int i = 0; i < argumentTypes.length; i++) {
				if (!_isTracked(argumentTypes[i])) {
					argumentOrigins.add(null);

					continue;
				}

				argumentOrigins.add(
					_getOrigin(
						frames, insnList, _getArgument(frame, i, count),
						new HashSet<>(visitedAbstractInsnNodes)));
			}

			origin.addReference(
				_addCallReference(
					argumentOrigins,
					_getMethodKey(
						methodInsnNode.owner, methodInsnNode.name,
						methodInsnNode.desc)));

			return origin;
		}

		SourceValue sourceValue = null;

		if (methodInsnNode.getOpcode() == Opcodes.INVOKESTATIC) {
			sourceValue = _getArgument(frame, 0, count);
		}
		else {
			sourceValue = _getReceiver(frame, count);
		}

		Origin wrappedOrigin = _getOrigin(
			frames, insnList, sourceValue, visitedAbstractInsnNodes);

		wrappedOrigin.transformValues(function);

		return wrappedOrigin;
	}

	private Origin _getSpringOrigin(String reference) {
		int index = reference.indexOf(".<init>(");

		if (index == -1) {
			return null;
		}

		List<List<String>> argumentsList = _springConstructorArguments.get(
			reference.substring(0, index));

		if (argumentsList == null) {
			return null;
		}

		int poundIndex = reference.indexOf(CharPool.POUND);

		int argumentCount = Type.getArgumentCount(
			reference.substring(index + 7, poundIndex));
		int parameterIndex = GetterUtil.getInteger(
			reference.substring(poundIndex + 1));

		Origin origin = null;

		for (List<String> arguments : argumentsList) {
			if (arguments.size() != argumentCount) {
				continue;
			}

			if (origin == null) {
				origin = new Origin();
			}

			String value = arguments.get(parameterIndex);

			if (value == null) {
				origin.addUnresolvedReason(_getUnresolvedReason(reference));
			}
			else {
				origin.addValue(value);
			}
		}

		return origin;
	}

	private Set<String> _getSubtypeNames(String className) {
		if (_subtypeNames == null) {
			_subtypeNames = new HashMap<>();

			for (Map.Entry<String, String> entry :
					_superClassNames.entrySet()) {

				Set<String> subtypeNames = _subtypeNames.computeIfAbsent(
					entry.getValue(), key -> new TreeSet<>());

				subtypeNames.add(entry.getKey());
			}

			for (Map.Entry<String, List<String>> entry :
					_interfaceNames.entrySet()) {

				for (String interfaceName : entry.getValue()) {
					Set<String> subtypeNames = _subtypeNames.computeIfAbsent(
						interfaceName, key -> new TreeSet<>());

					subtypeNames.add(entry.getKey());
				}
			}
		}

		Set<String> subtypeNames = new TreeSet<>();

		Deque<String> classNames = new ArrayDeque<>();

		classNames.push(className);

		while (!classNames.isEmpty()) {
			String curClassName = classNames.pop();

			if (!subtypeNames.add(curClassName)) {
				continue;
			}

			classNames.addAll(
				_subtypeNames.getOrDefault(
					curClassName, Collections.emptySet()));
		}

		return subtypeNames;
	}

	private Set<String> _getSupertypeNames(String className) {
		Set<String> supertypeNames = new TreeSet<>();

		Deque<String> classNames = new ArrayDeque<>();

		classNames.push(className);

		while (!classNames.isEmpty()) {
			String curClassName = classNames.pop();

			if (!supertypeNames.add(curClassName)) {
				continue;
			}

			String superClassName = _superClassNames.get(curClassName);

			if (superClassName != null) {
				classNames.push(superClassName);
			}

			classNames.addAll(
				_interfaceNames.getOrDefault(
					curClassName, Collections.emptyList()));
		}

		return supertypeNames;
	}

	private String _getUnresolvedReason(String reference) {
		int index = reference.indexOf(CharPool.POUND);

		if (index != -1) {
			String methodName = reference.substring(
				0, reference.indexOf(CharPool.OPEN_PARENTHESIS));

			if (methodName.endsWith(".<init>")) {
				methodName =
					"the constructor of " +
						StringUtil.removeLast(methodName, ".<init>");
			}

			return StringBundler.concat(
				"parameter ",
				GetterUtil.getInteger(reference.substring(index + 1)) + 1,
				" of ", methodName);
		}

		index = reference.indexOf(CharPool.OPEN_PARENTHESIS);

		if (index != -1) {
			return "the return value of " + reference.substring(0, index) +
				"()";
		}

		return "the field " + reference;
	}

	private String _getVisitedKey(
		String reference, ResolutionContext resolutionContext) {

		if (resolutionContext == null) {
			return reference;
		}

		return resolutionContext.getId() + StringPool.COLON + reference;
	}

	private boolean _isBootstrappedBy(
		InvokeDynamicInsnNode invokeDynamicInsnNode, String owner) {

		Handle handle = invokeDynamicInsnNode.bsm;

		return Objects.equals(handle.getOwner(), owner);
	}

	private boolean _isClassFileName(String name) {
		if (name.startsWith("com/liferay/") && name.endsWith(".class")) {
			return true;
		}

		return false;
	}

	private boolean _isFIPSEnabled(AbstractInsnNode abstractInsnNode) {
		if (abstractInsnNode.getOpcode() != Opcodes.GETSTATIC) {
			return false;
		}

		FieldInsnNode fieldInsnNode = (FieldInsnNode)abstractInsnNode;

		if (fieldInsnNode.owner.endsWith("/PropsValues") &&
			Objects.equals(fieldInsnNode.name, "FIPS_ENABLED")) {

			return true;
		}

		return false;
	}

	private boolean _isScanned(String bundleSymbolicName) {
		if ((_bundleSymbolicNames == null) ||
			_bundleSymbolicNames.contains(bundleSymbolicName)) {

			return true;
		}

		return false;
	}

	private boolean _isSecretKeySpec(TypeInsnNode typeInsnNode) {
		return Objects.equals(typeInsnNode.desc, _CLASS_NAME_SECRET_KEY_SPEC);
	}

	private boolean _isTracked(Type type) {
		if (Objects.equals(type, Type.INT_TYPE) ||
			Objects.equals(type, Type.getType(String.class)) ||
			_keyClassNames.contains(type.getInternalName())) {

			return true;
		}

		return false;
	}

	private boolean _isXMLFileName(String name) {
		if (name.startsWith("META-INF/") && name.endsWith(".xml")) {
			return true;
		}

		return false;
	}

	private Origin _resolve(
		Origin origin, ResolutionContext resolutionContext,
		Set<String> visitedReferences) {

		Origin resolvedOrigin = new Origin();

		resolvedOrigin.addAll(origin);

		resolvedOrigin.clearReferences();

		for (String reference : origin.getReferences()) {
			if (!visitedReferences.add(
					_getVisitedKey(reference, resolutionContext))) {

				continue;
			}

			if (reference.startsWith(_CALL_REFERENCE_PREFIX)) {
				resolvedOrigin.addAll(
					_resolveCall(
						_callSites.get(reference), resolutionContext,
						visitedReferences));

				continue;
			}

			int index = reference.indexOf(CharPool.POUND);

			if (index != -1) {
				ResolutionContext boundResolutionContext =
					_getBoundResolutionContext(
						reference.substring(0, index), resolutionContext);

				if (boundResolutionContext != null) {
					List<Origin> argumentOrigins =
						boundResolutionContext.getArgumentOrigins();

					Origin argumentOrigin = argumentOrigins.get(
						GetterUtil.getInteger(reference.substring(index + 1)));

					if (argumentOrigin == null) {
						resolvedOrigin.addUnresolvedReason(
							_getUnresolvedReason(reference));
					}
					else {
						resolvedOrigin.addAll(
							_resolve(
								argumentOrigin,
								boundResolutionContext.
									getParentResolutionContext(),
								visitedReferences));
					}

					continue;
				}
			}

			Origin referenceOrigin = _getReferenceOrigin(reference);

			if (referenceOrigin == null) {
				resolvedOrigin.addUnresolvedReason(
					_getUnresolvedReason(reference));

				continue;
			}

			ResolutionContext referenceResolutionContext = null;

			if (reference.startsWith(_COMPUTED_REFERENCE_PREFIX)) {
				referenceResolutionContext = resolutionContext;
			}

			Origin resolvedReferenceOrigin = _resolve(
				referenceOrigin, referenceResolutionContext, visitedReferences);

			if (reference.startsWith(_COMPUTED_REFERENCE_PREFIX)) {
				resolvedReferenceOrigin = _toComputedOrigin(
					resolvedReferenceOrigin);
			}

			resolvedOrigin.addAll(resolvedReferenceOrigin);
		}

		return resolvedOrigin;
	}

	private Origin _resolveCall(
		CallSite callSite, ResolutionContext resolutionContext,
		Set<String> visitedReferences) {

		Origin origin = new Origin();

		int depth = 1;

		if (resolutionContext != null) {
			depth = resolutionContext.getDepth() + 1;
		}

		if (depth > _MAX_DEPTH) {
			origin.addUnresolvedReason(
				"a value that passes through more than " + _MAX_DEPTH +
					" nested calls");

			return origin;
		}

		String methodKey = callSite.getMethodKey();

		boolean found = false;

		for (String implementationMethodKey :
				_getImplementationMethodKeys(methodKey)) {

			ResolutionContext boundResolutionContext =
				_getBoundResolutionContext(
					implementationMethodKey, resolutionContext);

			if (boundResolutionContext != null) {
				found = true;

				continue;
			}

			Origin returnOrigin = _referenceOrigins.get(
				implementationMethodKey);

			if (returnOrigin == null) {
				continue;
			}

			found = true;

			origin.addAll(
				_resolve(
					returnOrigin,
					new ResolutionContext(
						callSite.getArgumentOrigins(), depth,
						_resolutionContextCount++, implementationMethodKey,
						resolutionContext),
					visitedReferences));
		}

		if (!found) {
			origin.addUnresolvedReason(_getUnresolvedReason(methodKey));
		}

		return origin;
	}

	private void _scan(
		ClassNode classNode, String bundleSymbolicName, String featureName) {

		String className = _getClassName(classNode.name);

		String keyName = _getKeyName(classNode.name);

		if (classNode.superName != null) {
			_superClassNames.put(keyName, _getKeyName(classNode.superName));
		}

		List<String> interfaceNames = new ArrayList<>();

		for (String interfaceName : classNode.interfaces) {
			interfaceNames.add(_getKeyName(interfaceName));
		}

		_interfaceNames.put(keyName, interfaceNames);

		for (MethodNode methodNode : classNode.methods) {
			_methodAccesses.put(
				_getMethodKey(classNode.name, methodNode.name, methodNode.desc),
				methodNode.access);
		}

		for (MethodNode methodNode : classNode.methods) {
			if (methodNode.instructions.size() == 0) {
				continue;
			}

			List<MethodInsnNode> methodInsnNodes = _getCryptoMethodInsnNodes(
				classNode, methodNode);

			Frame<SourceValue>[] frames = null;

			try {
				frames = _analyze(classNode, methodNode);
			}
			catch (AnalyzerException analyzerException) {
				for (MethodInsnNode methodInsnNode : methodInsnNodes) {
					Origin origin = new Origin();

					origin.addUnresolvedReason(
						"bytecode that the scan cannot analyze: " +
							analyzerException.getMessage());

					_scannedCryptoCalls.add(
						new ScannedCryptoCall(
							origin,
							new CryptoCall(
								bundleSymbolicName, className, featureName,
								_getJCAClassName(methodInsnNode)),
							new Origin(), new Origin()));
				}

				continue;
			}

			_scanAssignments(classNode, frames, methodNode);

			InsnList insnList = methodNode.instructions;

			for (MethodInsnNode methodInsnNode : methodInsnNodes) {
				if (frames[insnList.indexOf(methodInsnNode)] == null) {
					continue;
				}

				String jcaClassName = _getJCAClassName(methodInsnNode);

				CryptoCall cryptoCall = new CryptoCall(
					bundleSymbolicName, className, featureName, jcaClassName);

				cryptoCall.setCryptoFunctions(
					_getCryptoFunctions(
						frames, insnList, jcaClassName, methodInsnNode));
				cryptoCall.setRelatedCryptoMaterialTypes(
					_getRelatedCryptoMaterialTypes(
						frames, insnList, jcaClassName, methodInsnNode));

				_scannedCryptoCalls.add(
					new ScannedCryptoCall(
						_getAlgorithmOrigin(frames, insnList, methodInsnNode),
						cryptoCall,
						_getKeySizeOrigin(frames, insnList, methodInsnNode),
						_getMethodOrigin(classNode, methodNode)));
			}
		}
	}

	private void _scan(JarInputStream jarInputStream, String name)
		throws IOException {

		Manifest manifest = jarInputStream.getManifest();

		String bundleSymbolicName = _getBundleSymbolicName(name, manifest);

		if (!_isScanned(bundleSymbolicName)) {
			return;
		}

		String featureName = _getFeatureName(name, manifest);

		JarEntry jarEntry = null;

		while ((jarEntry = jarInputStream.getNextJarEntry()) != null) {
			String jarEntryName = jarEntry.getName();

			if (_isClassFileName(jarEntryName)) {
				scan(bundleSymbolicName, featureName, jarInputStream);
			}
			else if (_isXMLFileName(jarEntryName)) {
				_scanSpringBeans(jarInputStream);
			}
		}
	}

	private void _scanAssignments(
		ClassNode classNode, Frame<SourceValue>[] frames,
		MethodNode methodNode) {

		String methodKey = _getMethodKey(
			classNode.name, methodNode.name, methodNode.desc);

		boolean returnValueTracked = _isTracked(
			Type.getReturnType(methodNode.desc));

		InsnList insnList = methodNode.instructions;

		for (AbstractInsnNode abstractInsnNode : insnList) {
			if (frames[insnList.indexOf(abstractInsnNode)] == null) {
				continue;
			}

			int opcode = abstractInsnNode.getOpcode();

			if ((opcode == Opcodes.PUTFIELD) || (opcode == Opcodes.PUTSTATIC)) {
				FieldInsnNode fieldInsnNode = (FieldInsnNode)abstractInsnNode;

				if (_isTracked(Type.getType(fieldInsnNode.desc))) {
					_track(
						_getFieldKey(fieldInsnNode),
						_getArgumentOrigin(
							frames, insnList, abstractInsnNode, 0, 1));
				}
			}
			else if (((opcode == Opcodes.ARETURN) ||
					  (opcode == Opcodes.IRETURN)) &&
					 returnValueTracked) {

				_track(
					methodKey,
					_getArgumentOrigin(
						frames, insnList, abstractInsnNode, 0, 1));
			}
			else if ((abstractInsnNode instanceof InvokeDynamicInsnNode) &&
					 _isBootstrappedBy(
						 (InvokeDynamicInsnNode)abstractInsnNode,
						 "java/lang/invoke/LambdaMetafactory")) {

				InvokeDynamicInsnNode invokeDynamicInsnNode =
					(InvokeDynamicInsnNode)abstractInsnNode;

				Handle handle = (Handle)invokeDynamicInsnNode.bsmArgs[1];

				int offset = 1;

				if ((handle.getTag() == Opcodes.H_INVOKESTATIC) ||
					(handle.getTag() == Opcodes.H_NEWINVOKESPECIAL)) {

					offset = 0;
				}

				String lambdaMethodKey = _getMethodKey(
					handle.getOwner(), handle.getName(), handle.getDesc());

				Type[] argumentTypes = Type.getArgumentTypes(
					invokeDynamicInsnNode.desc);

				for (int i = offset; i < argumentTypes.length; i++) {
					if (!_isTracked(argumentTypes[i])) {
						continue;
					}

					_track(
						lambdaMethodKey + StringPool.POUND + (i - offset),
						_getArgumentOrigin(
							frames, insnList, invokeDynamicInsnNode, i,
							argumentTypes.length));
				}
			}
			else if (abstractInsnNode instanceof MethodInsnNode) {
				MethodInsnNode methodInsnNode =
					(MethodInsnNode)abstractInsnNode;

				Type[] argumentTypes = Type.getArgumentTypes(
					methodInsnNode.desc);

				for (int i = 0; i < argumentTypes.length; i++) {
					if (!_isTracked(argumentTypes[i])) {
						continue;
					}

					_track(
						StringBundler.concat(
							_getMethodKey(
								methodInsnNode.owner, methodInsnNode.name,
								methodInsnNode.desc),
							StringPool.POUND, i),
						_getArgumentOrigin(
							frames, insnList, methodInsnNode, i,
							argumentTypes.length));
				}
			}
		}
	}

	private void _scanSpringBeans(InputStream inputStream) throws IOException {
		XMLInputFactory xmlInputFactory = XMLInputFactory.newDefaultFactory();

		xmlInputFactory.setProperty(
			XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.FALSE);
		xmlInputFactory.setProperty(XMLInputFactory.SUPPORT_DTD, Boolean.FALSE);

		try {
			XMLStreamReader xmlStreamReader =
				xmlInputFactory.createXMLStreamReader(
					new ByteArrayInputStream(inputStream.readAllBytes()));

			int eventType = xmlStreamReader.next();

			while (eventType != XMLStreamConstants.START_ELEMENT) {
				eventType = xmlStreamReader.next();
			}

			if (!Objects.equals(xmlStreamReader.getLocalName(), "beans")) {
				return;
			}

			Deque<Map<Integer, String>> argumentsDeque = new ArrayDeque<>();
			Deque<String> classNames = new ArrayDeque<>();

			while (xmlStreamReader.hasNext()) {
				eventType = xmlStreamReader.next();

				if ((eventType == XMLStreamConstants.END_ELEMENT) &&
					Objects.equals(xmlStreamReader.getLocalName(), "bean")) {

					_addSpringBean(argumentsDeque.pop(), classNames.pop());

					continue;
				}

				if (eventType != XMLStreamConstants.START_ELEMENT) {
					continue;
				}

				String localName = xmlStreamReader.getLocalName();

				if (localName.equals("bean")) {
					argumentsDeque.push(new TreeMap<>());
					classNames.push(
						GetterUtil.getString(
							xmlStreamReader.getAttributeValue(null, "class")));
				}
				else if (localName.equals("constructor-arg") &&
						 !argumentsDeque.isEmpty()) {

					Map<Integer, String> arguments = argumentsDeque.peek();

					arguments.put(
						GetterUtil.getInteger(
							xmlStreamReader.getAttributeValue(null, "index"),
							arguments.size()),
						xmlStreamReader.getAttributeValue(null, "value"));
				}
			}
		}
		catch (XMLStreamException xmlStreamException) {
			throw new IOException(xmlStreamException);
		}
	}

	private Origin _toComputedOrigin(Origin origin) {
		Origin computedOrigin = new Origin();

		for (String propertyKey : origin.getPropertyKeys()) {
			computedOrigin.addPropertyKey(propertyKey);
		}

		for (String unresolvedReason : origin.getUnresolvedReasons()) {
			computedOrigin.addUnresolvedReason(unresolvedReason);
		}

		if (computedOrigin.isConstant()) {
			computedOrigin.addUnresolvedReason("a value the code computes");
		}

		return computedOrigin;
	}

	private List<CryptoCall> _toCryptoCalls(
		ScannedCryptoCall scannedCryptoCall) {

		CryptoCall cryptoCall = scannedCryptoCall.getCryptoCall();

		Origin algorithmOrigin = _resolve(
			scannedCryptoCall.getAlgorithmOrigin(), null, new HashSet<>());
		Origin keySizeOrigin = _resolve(
			scannedCryptoCall.getKeySizeOrigin(), null, new HashSet<>());

		List<CryptoCall> cryptoCalls = new ArrayList<>();

		SortedSet<String> keySizes = keySizeOrigin.getValues();
		SortedSet<String> values = algorithmOrigin.getValues();

		Set<String> selectedBy = new TreeSet<>();

		if (!values.isEmpty()) {
			selectedBy.addAll(algorithmOrigin.getPropertyKeys());

			Origin methodOrigin = _resolve(
				scannedCryptoCall.getMethodOrigin(), null, new HashSet<>());

			selectedBy.addAll(methodOrigin.getPropertyKeys());
		}

		for (String value : values) {
			CryptoCall valueCryptoCall = cryptoCall.createPart();

			valueCryptoCall.setAlgorithm(
				CryptoProviderUtil.getAlgorithmName(
					value, cryptoCall.getJCAClassName()));
			valueCryptoCall.setSelectedBy(new ArrayList<>(selectedBy));

			if ((values.size() == 1) && keySizeOrigin.isConstant() &&
				(keySizes.size() == 1)) {

				valueCryptoCall.setKeySize(
					GetterUtil.getInteger(keySizes.first()));
			}

			cryptoCalls.add(valueCryptoCall);
		}

		Set<String> propertyKeys = algorithmOrigin.getPropertyKeys();

		if (!propertyKeys.isEmpty()) {
			CryptoCall configuredCryptoCall = cryptoCall.createPart();

			List<String> configuredBy = new ArrayList<>(propertyKeys);

			for (String propertyKey : keySizeOrigin.getPropertyKeys()) {
				if (configuredBy.contains(propertyKey)) {
					continue;
				}

				configuredBy.add(propertyKey);
			}

			configuredCryptoCall.setConfiguredBy(configuredBy);

			cryptoCalls.add(configuredCryptoCall);
		}

		Set<String> unresolvedReasons = algorithmOrigin.getUnresolvedReasons();

		if (!unresolvedReasons.isEmpty() || cryptoCalls.isEmpty()) {
			CryptoCall unresolvedCryptoCall = cryptoCall.createPart();

			if (unresolvedReasons.isEmpty()) {
				unresolvedCryptoCall.setUnresolvedReasons(
					Collections.singletonList("a value the code never sets"));
			}
			else {
				unresolvedCryptoCall.setUnresolvedReasons(
					new ArrayList<>(unresolvedReasons));
			}

			cryptoCalls.add(unresolvedCryptoCall);
		}

		return cryptoCalls;
	}

	private void _track(String reference, Origin origin) {
		Origin referenceOrigin = _referenceOrigins.get(reference);

		if (referenceOrigin == null) {
			_referenceOrigins.put(reference, origin);
		}
		else {
			referenceOrigin.addAll(origin);
		}
	}

	private static final String _CALL_REFERENCE_PREFIX = "call:";

	private static final String _CLASS_NAME_DIGESTER_UTIL =
		"com/liferay/portal/kernel/util/DigesterUtil";

	private static final String _CLASS_NAME_LIFERAY_SECURE_RANDOM =
		"com/liferay/portal/kernel/security/SecureRandom";

	private static final String _CLASS_NAME_SECRET_KEY_SPEC =
		"javax/crypto/spec/SecretKeySpec";

	private static final String _CLASS_NAME_SECURE_RANDOM_UTIL =
		"com/liferay/portal/kernel/security/SecureRandomUtil";

	private static final String _COMPUTED_REFERENCE_PREFIX = "computed:";

	private static final String _DEFAULT_SECURE_RANDOM_ALGORITHM =
		CryptoProviderUtil.getDefaultSecureRandomAlgorithm();

	private static final int _MAX_DEPTH = 32;

	private static final Map<String, CryptoFunction> _cipherCryptoFunctions =
		HashMapBuilder.put(
			String.valueOf(Cipher.DECRYPT_MODE), CryptoFunction.DECRYPT
		).put(
			String.valueOf(Cipher.ENCRYPT_MODE), CryptoFunction.ENCRYPT
		).put(
			String.valueOf(Cipher.UNWRAP_MODE), CryptoFunction.DECRYPT
		).put(
			String.valueOf(Cipher.WRAP_MODE), CryptoFunction.ENCRYPT
		).build();
	private static final Set<String> _computingClassNames = SetUtil.fromArray(
		"com/liferay/petra/string/StringBundler",
		"com/liferay/petra/string/StringUtil",
		"com/liferay/portal/kernel/util/StringUtil", "java/lang/String");
	private static final Map<String, CryptoFunction> _cryptoFunctions =
		HashMapBuilder.put(
			"BCrypt", CryptoFunction.KEYDERIVE
		).put(
			"Crypt", CryptoFunction.DIGEST
		).put(
			"KeyAgreement", CryptoFunction.KEYDERIVE
		).put(
			"KeyFactory", CryptoFunction.OTHER
		).put(
			"KeyGenerator", CryptoFunction.KEYGEN
		).put(
			"KeyManagerFactory", CryptoFunction.OTHER
		).put(
			"KeyPairGenerator", CryptoFunction.KEYGEN
		).put(
			"KeyStore", CryptoFunction.OTHER
		).put(
			"Mac", CryptoFunction.TAG
		).put(
			"MessageDigest", CryptoFunction.DIGEST
		).put(
			"SecretKeyFactory", CryptoFunction.KEYDERIVE
		).put(
			"SecretKeySpec", CryptoFunction.OTHER
		).put(
			"SecureRandom", CryptoFunction.GENERATE
		).put(
			"SSLContext", CryptoFunction.OTHER
		).put(
			"TrustManagerFactory", CryptoFunction.OTHER
		).build();
	private static final Map<String, String> _fixedAlgorithmClassNames =
		HashMapBuilder.put(
			_CLASS_NAME_LIFERAY_SECURE_RANDOM + ".<init>", "SecureRandom"
		).put(
			"java/security/SecureRandom.<init>", "SecureRandom"
		).put(
			"jodd/util/BCrypt.hashpw", "BCrypt"
		).put(
			"org/vps/crypt/Crypt.crypt", "Crypt"
		).build();
	private static final Map<String, String> _fixedAlgorithms =
		HashMapBuilder.put(
			"jodd/util/BCrypt.hashpw", "BCRYPT"
		).put(
			"org/vps/crypt/Crypt.crypt", "UFC-CRYPT"
		).build();
	private static final Map<String, String> _jcaClassNames =
		HashMapBuilder.put(
			"java/security/KeyFactory", "KeyFactory"
		).put(
			"java/security/KeyPairGenerator", "KeyPairGenerator"
		).put(
			"java/security/KeyStore", "KeyStore"
		).put(
			"java/security/MessageDigest", "MessageDigest"
		).put(
			"java/security/SecureRandom", "SecureRandom"
		).put(
			"java/security/Signature", "Signature"
		).put(
			"javax/crypto/Cipher", "Cipher"
		).put(
			"javax/crypto/KeyAgreement", "KeyAgreement"
		).put(
			"javax/crypto/KeyGenerator", "KeyGenerator"
		).put(
			"javax/crypto/Mac", "Mac"
		).put(
			"javax/crypto/SecretKeyFactory", "SecretKeyFactory"
		).put(
			"javax/net/ssl/KeyManagerFactory", "KeyManagerFactory"
		).put(
			"javax/net/ssl/SSLContext", "SSLContext"
		).put(
			"javax/net/ssl/TrustManagerFactory", "TrustManagerFactory"
		).build();
	private static final Map<String, String> _jdkPropertyKeys =
		HashMapBuilder.put(
			"java/security/KeyStore.getDefaultType", "keystore.type"
		).put(
			"javax/net/ssl/KeyManagerFactory.getDefaultAlgorithm",
			"ssl.KeyManagerFactory.algorithm"
		).put(
			"javax/net/ssl/TrustManagerFactory.getDefaultAlgorithm",
			"ssl.TrustManagerFactory.algorithm"
		).build();
	private static final Set<String> _keyClassNames = SetUtil.fromArray(
		"java/security/Key", "java/security/PrivateKey",
		"java/security/PublicKey", "javax/crypto/SecretKey",
		_CLASS_NAME_SECRET_KEY_SPEC);
	private static final Map<String, RelatedCryptoMaterialType>
		_keyFactoryRelatedCryptoMaterialTypes = HashMapBuilder.put(
			"generatePrivate", RelatedCryptoMaterialType.PRIVATE_KEY
		).put(
			"generatePublic", RelatedCryptoMaterialType.PUBLIC_KEY
		).build();
	private static final Set<String> _keyMethodNames = SetUtil.fromArray(
		"generateKey", "generatePrivate", "generatePublic", "generateSecret",
		"getAlgorithm");
	private static final Map<String, String> _keySizeMethodNames =
		HashMapBuilder.put(
			"java/security/KeyPairGenerator", "initialize"
		).put(
			"javax/crypto/KeyGenerator", "init"
		).build();
	private static final Map<String, Function<String, String>>
		_stringFunctions = HashMapBuilder.<String, Function<String, String>>put(
			"com/liferay/petra/string/StringUtil.toLowerCase",
			StringUtil::toLowerCase
		).put(
			"com/liferay/petra/string/StringUtil.toUpperCase",
			StringUtil::toUpperCase
		).put(
			"com/liferay/portal/kernel/util/GetterUtil.getInteger",
			Function.identity()
		).put(
			"com/liferay/portal/kernel/util/GetterUtil.getString",
			Function.identity()
		).put(
			"com/liferay/portal/kernel/util/StringUtil.toLowerCase",
			StringUtil::toLowerCase
		).put(
			"com/liferay/portal/kernel/util/StringUtil.toUpperCase",
			StringUtil::toUpperCase
		).put(
			"com/liferay/portal/kernel/util/StringUtil.trim", StringUtil::trim
		).put(
			"java/lang/String.toLowerCase", StringUtil::toLowerCase
		).put(
			"java/lang/String.toUpperCase", StringUtil::toUpperCase
		).put(
			"java/lang/String.trim", StringUtil::trim
		).build();

	private final Set<String> _bundleSymbolicNames;
	private int _callReferenceCount;
	private final Map<String, CallSite> _callSites = new HashMap<>();
	private int _computedReferenceCount;
	private final boolean _fipsEnabled;
	private final Map<String, List<String>> _interfaceNames = new HashMap<>();
	private final Map<String, Integer> _methodAccesses = new HashMap<>();
	private final Map<String, Origin> _referenceOrigins = new HashMap<>();
	private int _resolutionContextCount = 1;
	private final List<ScannedCryptoCall> _scannedCryptoCalls =
		new ArrayList<>();
	private final Map<String, List<List<String>>> _springConstructorArguments =
		new HashMap<>();
	private Map<String, Set<String>> _subtypeNames;
	private final Map<String, String> _superClassNames = new HashMap<>();

	private static class CallSite {

		public CallSite(List<Origin> argumentOrigins, String methodKey) {
			_argumentOrigins = argumentOrigins;
			_methodKey = methodKey;
		}

		public List<Origin> getArgumentOrigins() {
			return _argumentOrigins;
		}

		public String getMethodKey() {
			return _methodKey;
		}

		private final List<Origin> _argumentOrigins;
		private final String _methodKey;

	}

	private static class Origin {

		public void addAll(Origin origin) {
			_propertyKeys.addAll(origin._propertyKeys);
			_references.addAll(origin._references);
			_unresolvedReasons.addAll(origin._unresolvedReasons);
			_values.addAll(origin._values);
		}

		public void addPropertyKey(String propertyKey) {
			_propertyKeys.add(propertyKey);
		}

		public void addReference(String reference) {
			_references.add(reference);
		}

		public void addUnresolvedReason(String unresolvedReason) {
			_unresolvedReasons.add(unresolvedReason);
		}

		public void addValue(String value) {
			_values.add(value);
		}

		public void clearReferences() {
			_references.clear();
		}

		public Set<String> getPropertyKeys() {
			return _propertyKeys;
		}

		public Set<String> getReferences() {
			return _references;
		}

		public Set<String> getUnresolvedReasons() {
			return _unresolvedReasons;
		}

		public SortedSet<String> getValues() {
			return _values;
		}

		public boolean isConstant() {
			if (_propertyKeys.isEmpty() && _references.isEmpty() &&
				_unresolvedReasons.isEmpty()) {

				return true;
			}

			return false;
		}

		public void transformValues(Function<String, String> function) {
			Set<String> values = new TreeSet<>();

			for (String value : _values) {
				values.add(function.apply(value));
			}

			_values.clear();

			_values.addAll(values);
		}

		private final Set<String> _propertyKeys = new TreeSet<>();
		private final Set<String> _references = new TreeSet<>();
		private final Set<String> _unresolvedReasons = new TreeSet<>();
		private final SortedSet<String> _values = new TreeSet<>();

	}

	private static class OriginInterpreter extends SourceInterpreter {

		public OriginInterpreter(String methodKey, MethodNode methodNode) {
			super(Opcodes.ASM9);

			_methodKey = methodKey;

			int local = 0;

			if ((methodNode.access & Opcodes.ACC_STATIC) == 0) {
				local++;
			}

			Type[] argumentTypes = Type.getArgumentTypes(methodNode.desc);

			for (int i = 0; i < argumentTypes.length; i++) {
				_parameterIndexes.put(local, i);

				local += argumentTypes[i].getSize();
			}
		}

		@Override
		public SourceValue copyOperation(
			AbstractInsnNode abstractInsnNode, SourceValue sourceValue) {

			return sourceValue;
		}

		@Override
		public SourceValue newParameterValue(
			boolean instanceMethod, int local, Type type) {

			Integer parameterIndex = _parameterIndexes.get(local);

			if (parameterIndex == null) {
				return super.newParameterValue(instanceMethod, local, type);
			}

			return new SourceValue(
				type.getSize(),
				new ParameterInsnNode(
					_methodKey + StringPool.POUND + parameterIndex));
		}

		private final String _methodKey;
		private final Map<Integer, Integer> _parameterIndexes = new HashMap<>();

	}

	private static class ParameterInsnNode extends AbstractInsnNode {

		public ParameterInsnNode(String parameterKey) {
			super(-1);

			_parameterKey = parameterKey;
		}

		@Override
		public void accept(MethodVisitor methodVisitor) {
		}

		@Override
		public AbstractInsnNode clone(Map<LabelNode, LabelNode> labelNodes) {
			return this;
		}

		public String getParameterKey() {
			return _parameterKey;
		}

		@Override
		public int getType() {
			return VAR_INSN;
		}

		private final String _parameterKey;

	}

	private static class ResolutionContext {

		public ResolutionContext(
			List<Origin> argumentOrigins, int depth, int id, String methodKey,
			ResolutionContext parentResolutionContext) {

			_argumentOrigins = argumentOrigins;
			_depth = depth;
			_id = id;
			_methodKey = methodKey;
			_parentResolutionContext = parentResolutionContext;
		}

		public List<Origin> getArgumentOrigins() {
			return _argumentOrigins;
		}

		public int getDepth() {
			return _depth;
		}

		public int getId() {
			return _id;
		}

		public String getMethodKey() {
			return _methodKey;
		}

		public ResolutionContext getParentResolutionContext() {
			return _parentResolutionContext;
		}

		private final List<Origin> _argumentOrigins;
		private final int _depth;
		private final int _id;
		private final String _methodKey;
		private final ResolutionContext _parentResolutionContext;

	}

	private static class ScannedCryptoCall {

		public ScannedCryptoCall(
			Origin algorithmOrigin, CryptoCall cryptoCall, Origin keySizeOrigin,
			Origin methodOrigin) {

			_algorithmOrigin = algorithmOrigin;
			_cryptoCall = cryptoCall;
			_keySizeOrigin = keySizeOrigin;
			_methodOrigin = methodOrigin;
		}

		public Origin getAlgorithmOrigin() {
			return _algorithmOrigin;
		}

		public CryptoCall getCryptoCall() {
			return _cryptoCall;
		}

		public Origin getKeySizeOrigin() {
			return _keySizeOrigin;
		}

		public Origin getMethodOrigin() {
			return _methodOrigin;
		}

		private final Origin _algorithmOrigin;
		private final CryptoCall _cryptoCall;
		private final Origin _keySizeOrigin;
		private final Origin _methodOrigin;

	}

}
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useField} from 'formik';
import {useCallback, useEffect, useRef, useState} from 'react';

import {RequestResult} from '../../../../services/ApiHelper';
import {FileSelectorStatus} from '../../../FileSelector';

export interface ValidationResponse {
	errorMessages: string[];
	success: boolean;
	tempFilePath: string;
}

const STATUS_CHANGE_DELAY = 1000;

const TRANSIENT_STATUSES: FileSelectorStatus[] = [
	'SUCCESS',
	'UPLOADING',
	'VALIDATING',
];

export function useFileSelector(
	name: string,
	uploadRequest: (
		file: File,
		signal?: AbortSignal
	) => Promise<RequestResult<ValidationResponse>>
) {
	const [field, meta, helpers] = useField(name);

	const {setError, setTouched, setValue} = helpers;

	const [status, setStatus] = useState<FileSelectorStatus>(
		field.value ? 'STABLE_SUCCESS' : 'IDLE'
	);

	const [selectedFile, setSelectedFile] = useState<File | undefined>(
		field.value instanceof File ? field.value : undefined
	);

	const [serverError, setServerError] = useState<string | undefined>();

	const abortControllerRef = useRef<AbortController | null>(null);
	const isMountedRef = useRef(true);
	const uploadTimeoutRef = useRef<NodeJS.Timeout | null>(null);

	const cleanupProcesses = useCallback(() => {
		abortControllerRef.current?.abort();

		if (uploadTimeoutRef.current) {
			clearTimeout(uploadTimeoutRef.current);
		}
	}, []);

	const handleRejection = useCallback(
		(message: string) => {
			setServerError(message);
			setStatus('ERROR');
			setSelectedFile(undefined);
			setValue(undefined);
			setTouched(true);
		},
		[setTouched, setValue]
	);

	const handleRemove = () => {
		cleanupProcesses();
		abortControllerRef.current = null;

		setStatus('IDLE');
		setSelectedFile(undefined);
		setValue(undefined);
		setServerError(undefined);
		setTouched(true);
	};

	const handleUpload = async (file: File) => {
		cleanupProcesses();

		const controller = new AbortController();
		abortControllerRef.current = controller;

		setStatus('UPLOADING');
		setServerError(undefined);
		setSelectedFile(file);
		setValue(undefined);

		try {
			const result = await uploadRequest(file, controller.signal);

			if (
				!isMountedRef.current ||
				controller.signal.aborted ||
				result.error === 'Aborted'
			) {
				return;
			}

			if (result.error || !result.data || result.data.success === false) {
				const errorMsg =
					result.data?.errorMessages?.[0] ||
					result.error ||
					Liferay.Language.get('an-unexpected-error-occurred');

				handleRejection(errorMsg);

				return;
			}

			setStatus('VALIDATING');

			uploadTimeoutRef.current = setTimeout(() => {
				if (isMountedRef.current && !controller.signal.aborted) {
					setStatus('SUCCESS');
					setValue(file);
					setTouched(true);
				}
			}, STATUS_CHANGE_DELAY);
		}
		catch (error: any) {
			if (
				!isMountedRef.current ||
				controller.signal.aborted ||
				error.name === 'AbortError'
			) {
				return;
			}

			handleRejection(
				error.message ||
					Liferay.Language.get('an-unexpected-error-occurred')
			);
		}
	};

	const validate = useCallback(
		(value: any) => {
			if (TRANSIENT_STATUSES.includes(status)) {
				return undefined;
			}

			if (serverError) {
				return serverError;
			}

			if (!value) {
				return Liferay.Language.get('this-field-is-required');
			}

			return undefined;
		},
		[status, serverError]
	);

	useEffect(() => {
		return () => {
			isMountedRef.current = false;
			cleanupProcesses();
		};
	}, [cleanupProcesses]);

	useEffect(() => {
		if (status === 'SUCCESS') {
			const timeout = setTimeout(() => {
				if (isMountedRef.current) {
					setStatus('STABLE_SUCCESS');
				}
			}, STATUS_CHANGE_DELAY);

			return () => clearTimeout(timeout);
		}
	}, [status]);

	useEffect(() => {
		const error = validate(field.value);

		if (error !== meta.error) {
			setError(error);
		}
	}, [validate, field.value, setError, meta.error]);

	return {
		handleRejection,
		handleRemove,
		handleUpload,
		selectedFile,
		status,
		validate,
	};
}

const CONFLICT_STATUS = 409;

export const getLifecycleSaveErrorMessage = (error?: {status?: number}) =>
	error?.status === CONFLICT_STATUS
		? Liferay.Language.get(
				'the-lifecycle-was-changed-by-another-user.-reload-the-page-to-see-the-latest-changes'
			)
		: Liferay.Language.get('an-unexpected-error-occurred');

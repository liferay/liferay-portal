export enum FeatureName {
	EventAnalysis = 'event analysis',
}

export const useLimitReached = ({
	data = [],
	featureName,
}: {
	data?: Array<{
		type?: string;
		name?: string;
		currentUsage: number;
		limit: number;
	}>;
	featureName: FeatureName;
}) => {
	const usage = data?.find(
		(item) =>
			(item['type'] || item['name'] || '').toLowerCase() ===
			featureName.toLowerCase()
	);

	if (!usage) {
		return false;
	}

	if (usage.limit === -1) {
		return false;
	}

	return usage.currentUsage >= usage.limit;
};

/**
 * Feature flags for osb-faro-web.
 *
 * Liferay Data Platform ships on its own release cadence, decoupled from the
 * portal master branch, so we cannot rely on `Liferay.Util.FeatureFlags`
 * (which tracks portal master). Instead, flags live here as a registry and
 * are resolved at module load time from `localStorage`, falling back to each
 * flag's default.
 *
 * They can be toggled at runtime through the hidden panel at
 * `/workspace/:groupId/settings/feature-flags`. Because the exported `const`
 * values below are resolved once when this module is first imported, toggling a
 * flag requires a page reload to take effect (the panel prompts for it).
 */

export const FEATURE_FLAGS_STORAGE_KEY = 'faro:feature-flags';

export type FeatureFlagKey =
	| 'ENABLE_ASSET_CARD'
	| 'ENABLE_BLOCKLIST_KEYWORDS'
	| 'ENABLE_COMMERCE'
	| 'ENABLE_DELETE_DATA_SOURCE_BUTTON'
	| 'ENABLE_FORM_ABANDONMENT'
	| 'ENABLE_REAL_TIME_SEGMENTS';

export interface FeatureFlagDefinition {
	defaultValue: boolean;
	key: FeatureFlagKey;
}

export const FEATURE_FLAGS: FeatureFlagDefinition[] = [
	{defaultValue: false, key: 'ENABLE_ASSET_CARD'},
	{defaultValue: false, key: 'ENABLE_BLOCKLIST_KEYWORDS'},
	{defaultValue: false, key: 'ENABLE_COMMERCE'},
	{defaultValue: true, key: 'ENABLE_DELETE_DATA_SOURCE_BUTTON'},
	{defaultValue: false, key: 'ENABLE_FORM_ABANDONMENT'},
	{defaultValue: false, key: 'ENABLE_REAL_TIME_SEGMENTS'},
];

const DEFAULTS = FEATURE_FLAGS.reduce(
	(acc, {defaultValue, key}) => {
		acc[key] = defaultValue;

		return acc;
	},
	{} as Record<FeatureFlagKey, boolean>
);

type FeatureFlagOverrides = Partial<Record<FeatureFlagKey, boolean>>;

function readOverrides(): FeatureFlagOverrides {
	try {
		return (
			JSON.parse(
				window.localStorage.getItem(FEATURE_FLAGS_STORAGE_KEY) || '{}'
			) || {}
		);
	}
	catch (error) {
		return {};
	}
}

/**
 * Resolves a flag's current value: the persisted override when present,
 * otherwise the registered default. Reads `localStorage` on every call, so it
 * always reflects the latest persisted state (used by the toggle panel).
 */
export function isFeatureFlagEnabled(key: FeatureFlagKey): boolean {
	const overrides = readOverrides();

	return key in overrides ? !!overrides[key] : !!DEFAULTS[key];
}

/**
 * Persists an override for a single flag. The new value only takes effect after
 * a page reload, since the `const` exports below are resolved at module load.
 */
export function setFeatureFlag(key: FeatureFlagKey, value: boolean): void {
	const overrides = readOverrides();

	overrides[key] = value;

	window.localStorage.setItem(
		FEATURE_FLAGS_STORAGE_KEY,
		JSON.stringify(overrides)
	);
}

/*
 * Load-time resolved flag values. These are what the rest of the app imports
 * and read just like the former `constants.ts` booleans. Changing a flag in the
 * panel requires a reload because these are evaluated only once, on import.
 */

export const ENABLE_ASSET_CARD = isFeatureFlagEnabled('ENABLE_ASSET_CARD');

export const ENABLE_BLOCKLIST_KEYWORDS = isFeatureFlagEnabled(
	'ENABLE_BLOCKLIST_KEYWORDS'
);

export const ENABLE_COMMERCE = isFeatureFlagEnabled('ENABLE_COMMERCE');

export const ENABLE_DELETE_DATA_SOURCE_BUTTON = isFeatureFlagEnabled(
	'ENABLE_DELETE_DATA_SOURCE_BUTTON'
);

export const ENABLE_FORM_ABANDONMENT = isFeatureFlagEnabled(
	'ENABLE_FORM_ABANDONMENT'
);

export const ENABLE_REAL_TIME_SEGMENTS = isFeatureFlagEnabled(
	'ENABLE_REAL_TIME_SEGMENTS'
);

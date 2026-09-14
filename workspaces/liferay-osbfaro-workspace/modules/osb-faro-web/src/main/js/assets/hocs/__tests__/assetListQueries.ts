import {getAssetListQueries} from '../assetListQueries';
import {
	DOWNLOADS_METRIC,
	SUBMISSIONS_METRIC,
	VIEWS_METRIC,
} from 'shared/util/pagination';

describe('getAssetListQueries', () => {
	it('should rank both lists by the same metric for most asset types', () => {
		expect(getAssetListQueries('blog')).toMatchObject({
			accountsMetric: VIEWS_METRIC,
			individualsMetric: VIEWS_METRIC,
		});

		expect(getAssetListQueries('document')).toMatchObject({
			accountsMetric: DOWNLOADS_METRIC,
			individualsMetric: DOWNLOADS_METRIC,
		});

		expect(getAssetListQueries('journal')).toMatchObject({
			accountsMetric: VIEWS_METRIC,
			individualsMetric: VIEWS_METRIC,
		});
	});

	// Forms are the exception, and the asymmetry is deliberate rather than a
	// copy of the accounts metric: accounts rank by views, individuals by
	// submissions.

	it('should rank form accounts by views and form individuals by submissions', () => {
		expect(getAssetListQueries('form')).toMatchObject({
			accountsMetric: VIEWS_METRIC,
			individualsMetric: SUBMISSIONS_METRIC,
		});
	});

	it('should give every asset type both queries', () => {
		['blog', 'document', 'form', 'journal', 'objectEntry'].forEach(
			(graphQLType) => {
				const {accountsQuery, individualsQuery} =
					getAssetListQueries(graphQLType);

				expect(accountsQuery).toBeDefined();
				expect(individualsQuery).toBeDefined();
			}
		);
	});

	it('should fall back to the object entry queries for an unknown type', () => {
		expect(getAssetListQueries('banana')).toBe(
			getAssetListQueries('objectEntry')
		);
	});
});

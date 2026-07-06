import {
	getActionFromEventId,
	getEventId,
	getSupportedApplicationIds
} from '../activity-keys';

describe('activity-keys', () => {
	describe('getSupportedApplicationIds', () => {
		it('should map each event action to the applicationIds that support it', () => {
			expect(getSupportedApplicationIds('click')).toEqual([
				'Blog',
				'WebContent'
			]);
			expect(getSupportedApplicationIds('comment')).toEqual(['Blog']);
			expect(getSupportedApplicationIds('download')).toEqual([
				'Document',
				'ObjectEntry'
			]);
			expect(getSupportedApplicationIds('impression')).toEqual([
				'Blog',
				'Document',
				'ObjectEntry',
				'WebContent'
			]);
			expect(getSupportedApplicationIds('submit')).toEqual(['Form']);
			expect(getSupportedApplicationIds('view')).toEqual([
				'Blog',
				'Document',
				'Form',
				'ObjectEntry',
				'WebContent'
			]);
		});

		it('should return every applicationId for an unknown action', () => {
			expect(getSupportedApplicationIds(undefined)).toEqual([
				'Blog',
				'Document',
				'Form',
				'ObjectEntry',
				'WebContent'
			]);
		});
	});

	describe('getEventId', () => {
		it('should resolve the analytics eventId for supported pairs', () => {
			expect(getEventId('WebContent', 'click')).toBe('webContentClicked');
			expect(getEventId('Blog', 'comment')).toBe('commentPosted');
			expect(getEventId('ObjectEntry', 'download')).toBe(
				'objectEntryDownloaded'
			);
			expect(getEventId('Page', 'view')).toBe('pageViewed');
		});

		it('should return an empty string for unsupported pairs', () => {
			expect(getEventId('Document', 'click')).toBe('');
			expect(getEventId('Form', 'download')).toBe('');
		});
	});

	describe('getActionFromEventId', () => {
		it('should resolve stored eventIds back to their generic action', () => {
			expect(getActionFromEventId('webContentClicked')).toBe('click');
			expect(getActionFromEventId('commentPosted')).toBe('comment');
			expect(getActionFromEventId('pageViewed')).toBe('view');
		});
	});
});

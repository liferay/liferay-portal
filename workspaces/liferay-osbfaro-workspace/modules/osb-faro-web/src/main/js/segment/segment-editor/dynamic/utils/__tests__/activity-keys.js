import {getActionFromEventId, getEventId} from '../activity-keys';

describe('activity-keys', () => {
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

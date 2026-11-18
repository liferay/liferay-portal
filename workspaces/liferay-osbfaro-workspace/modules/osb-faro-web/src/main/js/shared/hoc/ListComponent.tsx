import Table from 'shared/components/table';
import withPaginationBar from './WithPaginationBar';
import withToolbar from './WithToolbar';
import {compose} from 'redux';
import {withEmpty} from 'cerebro-shared/hocs/utils';
import {withError, withLoading} from './util';

const ListComponent = compose<any>(
	withToolbar({legacyDropdownRangeKey: false}),
	withPaginationBar(),
	withLoading({spacer: true}),
	withError({page: false}),
	withEmpty()
)(Table);

export default ListComponent;

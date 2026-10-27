import * as fieldMappingsAPI from 'shared/api/field-mappings';
import autobind from 'autobind-decorator';
import React from 'react';
import SelectInput from 'shared/components/SelectInput';
import Sticker from 'shared/components/Sticker';
import TextTruncate from 'shared/components/TextTruncate';
import {FieldContexts, FieldTypes} from 'shared/util/constants';
import {PropTypes} from 'prop-types';

const ITEM_DISPLAY_LANG_MAP = {
	[FieldTypes.Date]: Liferay.Language.get('field-avatar-date'),
	[FieldTypes.Number]: Liferay.Language.get('field-avatar-number'),
	[FieldTypes.Boolean]: Liferay.Language.get('field-avatar-boolean'),
	[FieldTypes.String]: Liferay.Language.get('field-avatar-string')
};

const STICKER_DISPLAY_MAP = {
	[FieldTypes.Boolean]: 'boolean',
	[FieldTypes.Date]: 'date',
	[FieldTypes.Number]: 'number',
	[FieldTypes.String]: 'string'
};

const Item = ({name, rawType}) => (
	<div className='field-display-root'>
		<Sticker
			circle
			className='flex-shrink-0 mr-2'
			display={STICKER_DISPLAY_MAP[rawType]}
			size='sm'
		>
			{ITEM_DISPLAY_LANG_MAP[rawType]}
		</Sticker>

		<div className='field-details'>
			<TextTruncate className='name' title={name} />
		</div>
	</div>
);

class SelectFieldInput extends React.Component {
	static defaultProps = {
		context: FieldContexts.Demographics
	};

	static propTypes = {
		context: PropTypes.string,
		groupId: PropTypes.string.isRequired
	};

	_selectInputRef = React.createRef();

	/**
	 * Public method for focusing the input and triggering the dropdown.
	 */
	focus() {
		this._selectInputRef.current.focus();
	}

	@autobind
	getItems(query) {
		const {context, groupId} = this.props;

		return fieldMappingsAPI
			.search({
				context,
				delta: 10,
				groupId,
				orderByType: '',
				query
			})
			.then(({items}) => items);
	}

	render() {
		return (
			<SelectInput
				{...this.props}
				dataSourceFn={this.getItems}
				itemRenderer={Item}
				placeholder={Liferay.Language.get('select-field')}
				ref={this._selectInputRef}
			/>
		);
	}
}

export default SelectFieldInput;

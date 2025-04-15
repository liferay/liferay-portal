package ${configYAML.apiPackagePath}.client.pagination;

import jakarta.annotation.Generated;

/**
 * @author ${configYAML.author}
 * @generated
 */
@Generated("")
public class Pagination {

	public static Pagination of(int page, int pageSize) {
		return new Pagination(page, pageSize);
	}

	public int getEndPosition() {
		if ((_page < 0) || (_pageSize < 0)) {
			return -1;
		}

		return _page * _pageSize;
	}

	public int getPage() {
		return _page;
	}

	public int getPageSize() {
		return _pageSize;
	}

	public int getStartPosition() {
		if ((_page < 0) || (_pageSize < 0)) {
			return -1;
		}

		return (_page - 1) * _pageSize;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("{\"page\": ");

		sb.append(_page);
		sb.append(", \"pageSize\": ");
		sb.append(_pageSize);
		sb.append("}");

		return sb.toString();
	}

	private Pagination(int page, int pageSize) {
		_page = page;
		_pageSize = pageSize;
	}

	private final int _page;
	private final int _pageSize;

}
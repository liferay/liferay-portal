package ${configYAML.apiPackagePath}.client.aggregation;

import jakarta.annotation.Generated;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ${configYAML.author}
 * @generated
 */
@Generated("")
public class Facet {

	public Facet() {
	}

	public Facet(String facetCriteria, List<FacetValue> facetValues) {
		_facetCriteria = facetCriteria;
		_facetValues = facetValues;
	}

	public String getFacetCriteria() {
		return _facetCriteria;
	}

	public List<FacetValue> getFacetValues() {
		return _facetValues;
	}

	public void setFacetCriteria(String facetCriteria) {
		_facetCriteria = facetCriteria;
	}

	public void setFacetValues(List<FacetValue> facetValues) {
		_facetValues = facetValues;
	}

	public static class FacetValue {

		public FacetValue() {
		}

		public FacetValue(Integer numberOfOccurrences, String term) {
			_numberOfOccurrences = numberOfOccurrences;
			_term = term;
		}

		public Integer getNumberOfOccurrences() {
			return _numberOfOccurrences;
		}

		public String getTerm() {
			return _term;
		}

		private Integer _numberOfOccurrences;
		private String _term;

	}

	private String _facetCriteria;
	private List<FacetValue> _facetValues = new ArrayList<>();

}
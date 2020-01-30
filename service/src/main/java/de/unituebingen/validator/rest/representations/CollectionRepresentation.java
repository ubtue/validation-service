package de.unituebingen.validator.rest.representations;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Abstract Representation of a collection page.
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@JsonPropertyOrder({ "links", "embedded" })
public abstract class CollectionRepresentation {

	// Conditions for visibility of hyperlinks
	public static final String FIRST_PAGE_CONDITION = "${instance.pageIndex > 0}";
	public static final String NEXT_PAGE_CONDITION = "${instance.pageIndex + 1 <= instance.lastPageIndex}";
	public static final String PREVIOUS_PAGE_CONDITION = "${instance.pageIndex -1 >= 0}";
	public static final String LAST_PAGE_CONDITION = "${instance.pageIndex < instance.lastPageIndex}";

	// Query parameters
	public static final String SELF_PAGE_PARAMETER = "?page=${instance.pageIndex}";
	public static final String FIRST_PAGE_PARAMETER = "?page=0";
	public static final String NEXT_PAGE_PARAMETER = "?page=${instance.pageIndex+1}";
	public static final String PREVIOUS_PAGE_PARAMETER = "?page=${instance.pageIndex-1}";
	public static final String LAST_PAGE_PARAMETER = "?page=${instance.lastPageIndex}";
	public static final String OTHER_PARAMETERS = "${instance.createQueryParametersAppendix()}";

	/** The index of the current result page */
	@XmlTransient
	@JsonIgnore
	protected int pageIndex;

	/**
	 * The default size of a result page (i.e. the general number of results
	 * contained)
	 */
	@XmlTransient
	@JsonIgnore
	protected int pageSize;

	/** Query parameters to be appended to the url */
	@XmlTransient
	@JsonIgnore
	protected Map<String, String> queryParameters = new HashMap<>();

	/** The index of the last page of the result set */
	@XmlTransient
	@JsonIgnore
	protected int lastPageIndex;

	/** The number of results embedded in this page */
	@XmlElement
	@JsonProperty
	protected int count;

	/** The total result number of the query */
	@XmlElement
	@JsonProperty
	protected long totalCount;

	@XmlElement
	@XmlElementWrapper(name = "embedded")
	@JsonProperty("_embedded")
	protected Map<String, Object> embedded = new HashMap<>();

	/**
	 * Constructor for creating a collection representation
	 * 
	 * @param totalCount
	 *            The amount of results in total.
	 * @param count
	 *            The amount of results to be embedded in this page.
	 * @param pageSize
	 *            The number of results per page in general
	 * @param pageIndex
	 *            The index of the page.
	 */
	public CollectionRepresentation(long totalCount, int count, int pageSize, int pageIndex) {
		super();
		this.setLastPageIndex(totalCount, pageSize);
		this.setPageIndex(pageIndex);
		this.totalCount = totalCount;
		this.count = count;
		this.pageSize = pageSize;
	}

	private int calculateLastPage(long totalCount, int pageSize) {
		return (int) Math.ceil((double) totalCount / (double) pageSize) - 1;
	}

	/**
	 * Add a query parameter to the url appendix.
	 * 
	 * @param key
	 *            url param key.
	 * @param value
	 *            url param value.
	 */
	public void addQueryParameter(String key, String value) {
		if (value != null && value.length() != 0) {
			this.queryParameters.put(key, value);
		}
	}

	/**
	 * Creates the query parameter appendix to be appended to the collection url.
	 * 
	 * @return The parameter appendix for the base url.
	 * @throws UnsupportedEncodingException
	 */
	public String createQueryParametersAppendix() throws UnsupportedEncodingException {
		StringBuffer buffer = new StringBuffer();
		for (Map.Entry<String, String> entry : this.queryParameters.entrySet()) {
			buffer.append("&");
			buffer.append(entry.getKey());
			buffer.append("=");
			buffer.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
		}
		return buffer.toString();
	}

	// Generated setters and getters

	public void setLastPageIndex(long totalCount, int pageSize) {
		this.lastPageIndex = calculateLastPage(totalCount, pageSize);
	}

	public int getPageIndex() {
		return pageIndex;
	}

	public int getLastPageIndex() {
		return lastPageIndex;
	}

	public void setPageIndex(int page) {
		this.pageIndex = page;
	}

	public Map<String, String> getQueryParameters() {
		return queryParameters;
	}

	public void setQueryParameters(Map<String, String> queryParameters) {
		this.queryParameters = queryParameters;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public long getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public Map<String, Object> getEmbedded() {
		return embedded;
	}

	public void setEmbedded(Map<String, Object> embedded) {
		this.embedded = embedded;
	}

}

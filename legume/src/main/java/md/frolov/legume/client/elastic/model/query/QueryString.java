package md.frolov.legume.client.elastic.model.query;

import com.google.web.bindery.autobean.shared.AutoBean;

/** @author Ivan Frolov (ifrolov@tacitknowledge.com) */
public interface QueryString extends Query
{
    @AutoBean.PropertyName("query_string")
    QueryStringDef getQueryString();
    @AutoBean.PropertyName("query_string")
    void setQueryString(QueryStringDef queryString);

    interface QueryStringDef
    {
        String getQuery();
        void setQuery(String query);

        @AutoBean.PropertyName("default_field")
        String getDefaultField();
        @AutoBean.PropertyName("default_field")
        void setDefaultField(String defaultField);

        @AutoBean.PropertyName("default_operator")
        String getDefaultOperator();
        @AutoBean.PropertyName("default_operator")
        void setDefaultOperator(String defaultOperator);

        String getAnalyzer();
        void setAnalyzer(String analyzer);

        @AutoBean.PropertyName("allow_leading_wildcard")
        String getAllowLeadingWildcard();
        @AutoBean.PropertyName("allow_leading_wildcard")
        void setAllowLeadingWildcard(String allowLeadingWildcard);

        @AutoBean.PropertyName("lowercase_expanded_terms")
        String getLowercaseExpandedTerms();
        @AutoBean.PropertyName("lowercase_expanded_terms")
        void setLowercaseExpandedTerms(String lowercaseExpandedTerms);

        @AutoBean.PropertyName("enable_position_increments")
        String getEnablePositionIncrements();
        @AutoBean.PropertyName("enable_position_increments")
        void setEnablePositionIncrements(String enablePositionIncrements);

        @AutoBean.PropertyName("fuzzy_max_expansions")
        String getFuzzyMaxExpansions();
        @AutoBean.PropertyName("fuzzy_max_expansions")
        void setFuzzyMaxExpansions(String fuzzyMaxExpansions);

        @AutoBean.PropertyName("fuzzy_min_sim")
        String getFuzzyMinSim();
        @AutoBean.PropertyName("fuzzy_min_sim")
        void setFuzzyMinSim(String fuzzyMinSim);

        @AutoBean.PropertyName("fuzzy_prefix_length")
        String getFuzzyPrefixLength();
        @AutoBean.PropertyName("fuzzy_prefix_length")
        void setFuzzyPrefixLength(String fuzzyPrefixLength);

        @AutoBean.PropertyName("phrase_slop")
        String getPhraseSlop();
        @AutoBean.PropertyName("phrase_slop")
        void setPhraseSlop(String phraseSlop);

        String getBoost();
        void setBoost(String boost);

        @AutoBean.PropertyName("analyze_wildcard")
        String getAnalyzeWildcard();
        @AutoBean.PropertyName("analyze_wildcard")
        void setAnalyzeWildcard(String analyzeWildcard);

        @AutoBean.PropertyName("auto_generate_phrase_queries")
        String getAutoGeneratePhraseQueries();
        @AutoBean.PropertyName("auto_generate_phrase_queries")
        void setAutoGeneratePhraseQueries(String autoGeneratePhraseQueries);

        @AutoBean.PropertyName("minimum_should_match")
        String getMinimumShouldMatch();
        @AutoBean.PropertyName("minimum_should_match")
        void setMinimumShouldMatch(String minimumShouldMatch);

        String getLenient();
        void setLenient(String lenient);
    }

    QueryString clone();
}

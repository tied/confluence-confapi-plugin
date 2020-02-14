package de.aservo.atlassian.confluence.confapi.bean.error;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@XmlRootElement
public class ErrorCollection {

    @XmlElement
    private Collection<String> errorMessages = new ArrayList<>();

    @XmlElement
    private Map<String, String> errors = new HashMap<>();

    private Integer status = null;

    public ErrorCollection addErrorMessage(String errorMessage) {
        if (errorMessage != null) {
            errorMessages.add(errorMessage);
        }
        return this;
    }

    public ErrorCollection addErrorMessages(Collection<String> messages) {
        if (messages != null) {
            messages.forEach(this::addErrorMessage);
        }
        return this;
    }

    public boolean hasAnyErrors() {
        return !errorMessages.isEmpty() || !errors.isEmpty();
    }

    public Collection<String> getErrorMessages() {
        return errorMessages;
    }

    public Map<String, String> getErrors() {
        return errors;
    }

    public Integer getStatus() {
        return status;
    }

    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public static ErrorCollection of(String... messages) {
        return of(Arrays.asList(messages));
    }

    public static ErrorCollection of(Collection<String> messages) {
        return (new ErrorCollection()).addErrorMessages(messages);
    }

}

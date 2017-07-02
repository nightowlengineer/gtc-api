package uk.org.gtc.api.domain;

import java.util.Date;

import org.mongojack.ObjectId;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BaseDomainObject
{
    @ObjectId
    @JsonProperty("_id")
    private String id;

    private Date createdDate;
    private Date lastUpdatedDate;

    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        final BaseDomainObject other = (BaseDomainObject) obj;
        if (createdDate == null)
        {
            if (other.createdDate != null)
            {
                return false;
            }
        }
        else if (!createdDate.equals(other.createdDate))
        {
            return false;
        }
        if (id == null)
        {
            if (other.id != null)
            {
                return false;
            }
        }
        else if (!id.equals(other.id))
        {
            return false;
        }
        if (lastUpdatedDate == null)
        {
            if (other.lastUpdatedDate != null)
            {
                return false;
            }
        }
        else if (!lastUpdatedDate.equals(other.lastUpdatedDate))
        {
            return false;
        }
        return true;
    }

    public Date getCreatedDate()
    {
        return createdDate;
    }

    public String getId()
    {
        return id;
    }

    public Date getLastUpdatedDate()
    {
        return lastUpdatedDate;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((createdDate == null) ? 0 : createdDate.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((lastUpdatedDate == null) ? 0 : lastUpdatedDate.hashCode());
        return result;
    }

    public void setCreatedDate(final Date createdDate)
    {
        this.createdDate = createdDate;
    }

    public void setId(final String id)
    {
        this.id = id;
    }

    public void setLastUpdatedDate(final Date lastUpdatedDate)
    {
        this.lastUpdatedDate = lastUpdatedDate;
    }

}

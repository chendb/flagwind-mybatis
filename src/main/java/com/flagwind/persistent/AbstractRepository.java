package com.flagwind.persistent;

import java.io.Serializable;

import com.flagwind.persistent.base.*;

/**
 * @author hbche
 */
public interface AbstractRepository<E, ID extends Serializable> extends
		BaseInsertRepository<E,ID>,
		BaseUpdateRepository<E>,
		BaseDeleteRepository<ID>,
		BaseSelectRepository<E,ID>{

}
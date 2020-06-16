package com.flagwind.persistent;

import com.flagwind.persistent.base.*;

import java.io.Serializable;

/**
 * @author hbche
 */

public interface AbstractRepository<E, ID extends Serializable> extends
//		BaseDynamicRepository,
		BaseInsertRepository<E,ID>,
		BaseUpdateRepository<E>,
		BaseDeleteRepository<ID>,
		BaseSelectRepository<E,ID>{


}
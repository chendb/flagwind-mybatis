package com.flagwind.persistent;

import com.flagwind.persistent.base.BaseDeleteRepository;
import com.flagwind.persistent.base.BaseInsertRepository;
import com.flagwind.persistent.base.BaseSelectRepository;
import com.flagwind.persistent.base.BaseUpdateRepository;

import java.io.Serializable;

/**
 * @author hbche
 */

public interface AbstractRepository<E, ID extends Serializable> extends
		BaseInsertRepository<E,ID>,
		BaseUpdateRepository<E>,
		BaseDeleteRepository<ID>,
		BaseSelectRepository<E,ID>{


}
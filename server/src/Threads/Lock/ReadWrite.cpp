#include <dashee//Threads/Lock/ReadWrite.h>

/**
 * Contruct our Lock
 */
dashee::Threads::LockReadWrite::LockReadWrite()
{
    this->rwlock = new pthread_rwlock_t();
    this->attr = new pthread_rwlockattr_t();

    if (pthread_rwlockattr_init(this->attr) != 0)
	throw ExceptionLock("Thread attribute init failed");

    if (pthread_rwlockattr_setpshared(this->attr, PTHREAD_PROCESS_SHARED) != 0)
	throw ExceptionLock("Thread attribute set failed");
    
    // Make sure that initilization comes out as good, otherwise we should
    // absolutly die die die
    int ec = pthread_rwlock_init(this->rwlock, this->attr);
    if (ec != 0)
	throw ExceptionLock(
		"Error Initilizing LockRead, ec='" + 
		dashee::itostr(ec) + "'. This should not happen!"
	    );

    if (pthread_rwlockattr_destroy(this->attr) != 0)
	throw ExceptionLock("Thread attribute destroy failed");

    this->attr = NULL;
}

/**
 * Lock our mutex.
 *
 * @param type Read or write?
 *
 * @throws ExceptionLock If exit code is not 0
 */
void dashee::Threads::LockReadWrite::lock(lockType type)
{
    int ec;
    if (type == LOCKTYPE_READ) 
	ec = pthread_rwlock_rdlock(this->rwlock);
    else if (type == LOCKTYPE_WRITE)
	ec = pthread_rwlock_wrlock(this->rwlock);
    else
	throw ExceptionLock("Invalid Lock type");

    switch (ec)
    {
	case 0:
	    return;
	    break;
	case EINVAL:
	    throw ExceptionLock(
		    "EINVAL thrown when locking, see man page for details."
		);
	    break;
	case EAGAIN:
	    throw ExceptionLock("Max number of Mutex locks reached.");
	    break;
	case EDEADLK:
	    throw ExceptionLockDeadlock(
		    "Cannot double lock the same mutex in one thread."
		);
	    break;
	default:
            throw ExceptionLock(
                    "Unknown error in Mutex::lock, ec='" + 
		    dashee::itostr(ec) + "'. This should not happen!"
                );
            break;
	
    }	
}

/**
 * Try lock ntimes. If ntimes is set to 0 then try lock forever
 *
 * @param ntimes The number of times to try locking, 0 is unlimited
 * @param npause The number of seconds to sleep in the case of failed try
 * @param type Read or write?
 *
 * @return TRUE if locked, FALSE if failed to lock given contraints
 */
bool dashee::Threads::LockReadWrite::trylock(
	int ntimes, 
	int npause,
	lockType type
    )
{
    int n = 0;
    int ec;
    while (true)
    {
	if (type == LOCKTYPE_READ) 
	    ec = pthread_rwlock_tryrdlock(this->rwlock);
	else if (type == LOCKTYPE_WRITE)
	    ec = pthread_rwlock_trywrlock(this->rwlock);
	else
	    throw ExceptionLock("Invalid Lock type");

	// Handle errors
	switch (ec)
	{
	    case 0:
		return true;
		break;

	    case EBUSY:
		dashee::sleep(npause);

		if (ntimes != 0)
		{
		    if (n <= ntimes)
			return false;

		    n++;
		}
		break;

	    case EINVAL:
		throw ExceptionLock(
			"EINVAL thrown when trying lock, see man page for "
			"details."
		    );
		break;
	    case EAGAIN:
		throw ExceptionLock("Max number of LockRead locks reached.");
		break;
	    default:
		throw ExceptionLock(
			"Unknown error in LockRead::trylock, ec='" + 
			dashee::itostr(ec) + "'. This should not happen!"
		    );
		break;
	}	
    }	

    return false;
}

/**
 * Unlock our read lock.
 *
 * @throws ExceptionLock If exit code is not 0
 */
void dashee::Threads::LockReadWrite::unlock()
{
    int ec = pthread_rwlock_unlock(this->rwlock);

    switch (ec)
    {
	case 0:
	    return;
	    break;
	case EINVAL:
	    throw ExceptionLock(
		    "EINVAL thrown when locking, see man page for details."
		);
	    break;
	case EAGAIN:
	    throw ExceptionLock("Max number of Mutex locks reached.");
	    break;
	case EPERM:
	    throw ExceptionLock(
		    "Cannot unlock neighbours mutex, Mutex must be owned by "
		    "this thread by using Mutex::lock before Mutex::unlock can "
		    "be called"
		);
	    break;
	default:
            throw ExceptionLock(
                    "Unknown error in Mutex::unlock, ec='" + 
		    dashee::itostr(ec) + "'. This should not happen!"
                );
            break;
	
    }	
}

/**
 * Delete our mutex
 */
dashee::Threads::LockReadWrite::~LockReadWrite()
{
    pthread_rwlock_destroy(this->rwlock);
    delete this->rwlock;
}   

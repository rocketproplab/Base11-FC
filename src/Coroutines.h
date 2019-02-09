
void InitTasks();

unsigned EventCatch(unsigned);
void     EventRaise(unsigned);
void     NextTask();
void     CreateTask(void (*entry)());
unsigned Microseconds(void);
void     TimerWait(unsigned ms);

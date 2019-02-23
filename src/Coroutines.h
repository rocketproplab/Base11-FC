#ifndef COROUTINE_GAURD
#define COROUTINE_GAURD

// Sent when the engine turns on
#define ENGINE_ON 1 << 0

// Sent when the engine is turned off
#define ENGINE_OFF 1 << 1

void InitTasks();

unsigned EventCatch(unsigned);
void     EventRaise(unsigned);
void     NextTask();
void     CreateTask(void ( *entry )());
unsigned Microseconds(void);
void     TimerWait(unsigned ms);

#endif

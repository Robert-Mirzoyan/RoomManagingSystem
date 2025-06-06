# Connection Pooling

## Why is it needed?

- Creating and closing DB connections is expensive
- Pooling reuses connections efficiently
- Many connections can be used at same time
- Reduces the load on the database server

## Advantages
- Performance: Reuse open connections
- Scalability: Handle many requests with fewer resources
- Resource Management: Avoid exhausting DB connection limits

## Disadvantages
- Slight memory usage increase for pool management
- Little more complex as configuration and monitoring needed

## Comparison: Single connection vs Pooling

In both cases 5 threads with time of 3 seconds were used. For implemented single connection 
data source it took 15.084 seconds to complete 5 threads as they use same connection and one must wait
for other to finish before it starts(otherwise we get error). I used CountDownLatch() for making
threads wait for one another before starting(to avoid errors). For the pooling I used HikariCP,
and it took 3.19 seconds to finish all as threads there have their own connection, and they are done parallel.

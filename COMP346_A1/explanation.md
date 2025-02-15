## Reason why with higher network buffer that we get a boost in running time.
- One reason for an increase in running time is since we have a bigger size buffer, the 
program doesn't have to context switch and yield as much, making it a little bit faster.
(for me I did not get a significant boost, n=10 I get around 15-18ms, n=20 I get 14-17ms)
- Running time for this assignment is heavily dependent on CPU
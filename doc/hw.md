1. FizzBuzz is a very common interview question. Print out the numbers from 1 -100, but with a twist – if the number is divisible by 3, print “Fizz” instead of the number. If the number is divisible by 5, print “Buzz” instead of the number. If the number is divisible by both, print “Fizz Buzz” instead of the number. This is a bit challenging because our version of BASIC doesn’t directly support modulo. But there is a way – consider rules of integer division…

```BASIC
FOR i = 1 to 100
mod3 = i - (i / 3 * 3)
mod5 = i - (i / 5 * 5)

IF mod3 + mod5 == 0 THEN fzbz
IF mod3 == 0 THEN fizz
IF mod5 == 0 THEN buzz
back:
NEXT i
END

fizz:
PRINT "Fizz"
GOTO back

buzz:
PRINT "Buzz"
GOTO back

fzbz:
PRINT "Fizz Buzz"
GOTO back
```

2. The Collatz Conjecture – ask the user for a number greater than 1. Find the number of steps it takes to reach 1 by the following rules: if the number is even, divide it by 2. If it is odd, multiply it by 3, then add 1. Print the number of steps.

```BASIC
INPUT "Please provide a number > 1.", num
steps = 0
WHILE num > 1 finish
mod2 = num - (num / 2 * 2)
IF mod2==0 THEN even
IF mod2==1 THEN odd
back:
steps = steps + 1

finish:
PRINT steps, " steps."
END

odd:
num = 3 * num + 1
GOTO back

even
num = num / 2
GOTO back

```

3. Class average – create DATA for a small class of 5 students – the first element is the students name, the rest of the DATA is a list of grades. For each student, print their average.

```BASIC
DATA "Amy", 98, 93, 91, 73, 85
GOSUB printavg
DATA "Ben", 98, 93, 91, 73, 85
GOSUB printavg
DATA "Cal", 98, 93, 91, 73, 85
GOSUB printavg
DATA "Dan", 98, 93, 91, 73, 85
GOSUB printavg
DATA "Eve", 98, 93, 91, 73, 85
GOSUB printavg
END

printavg:
READ name$, a, b, c, d, e
avg = (a+b+c+d+e)/5
PRINT name$, ": ", avg
RETURN
```

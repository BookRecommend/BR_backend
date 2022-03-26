package bookrecommend.searcher.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;


@Aspect
public class TimeTraceAop {
    @Around("execution(* bookrecommend.searcher..*(..)) && !target(bookrecommend.searcher.SpringConfig)")

    public Object execute(ProceedingJoinPoint joinPoint) throws Throwable{
        long start = System.currentTimeMillis();
        System.out.println("START: "+ joinPoint.toString());

        try{
            return joinPoint.proceed();
        }finally{
            long finish = System.currentTimeMillis();
            long elapsedTime = finish - start;

            System.out.println("END: "+ joinPoint.toString()+ " " +elapsedTime+"ms");
        }
    }
}

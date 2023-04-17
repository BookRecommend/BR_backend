package bookrecommend.searcher.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Aspect
public class TimeTraceAop {

    private final Logger logger =  LoggerFactory.getLogger(this.getClass());

    @Around("execution(* bookrecommend.searcher..*(..)) && !target(bookrecommend.searcher.SpringConfig)")
    public Object execute(ProceedingJoinPoint joinPoint) throws Throwable{
        long start = System.currentTimeMillis();
        logger.info(" START: "+ joinPoint.toString());
               try{
            return joinPoint.proceed();
        }finally{
            long finish = System.currentTimeMillis();
            long elapsedTime = finish - start;

            logger.info("END: "+ joinPoint+ " " +elapsedTime+"ms");

        }
    }
}

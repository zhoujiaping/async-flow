package async

import groovy.sql.Sql

import java.sql.Connection
import java.util.concurrent.TimeUnit

class TaskRepo {
    def local = [
            url     : 'jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=UTF-8',
            user    : 'root',
            password: ''
    ]
    def config = local
    def driver = 'com.mysql.jdbc.Driver'
    def sql = Sql.newInstance(config.url, config.user, config.password, driver)

    def insertTask(Task task, Closure closure) {
        sql.withTransaction {
            conn ->
                withTx(conn) {
                    sql.executeInsert("insert into ... values (...)")
                }
        }
    }

    def insertTask(long delay, TimeUnit unit, Task task, Closure closure) {
        sql.withTransaction {
            conn ->
                withTx(conn) {
                    sql.executeInsert("insert into ... values (...)")
                }
        }
    }

    def updateTaskToDone(Task task) {
        sql.withTransaction {
            conn ->
                withTx(conn) {
                    sql.executeUpdate("update ... set xx=xx where x=x")
                }
        }
    }

    def updateTaskToError(Exception e) {
        sql.withTransaction {
            conn ->
                withTx(conn) {
                    sql.executeUpdate("update ... set xx=xx where x=x")
                }
        }
    }

    def updateTaskToProcessing(Exception e) {
        sql.withTransaction {
            conn ->
                withTx(conn) {
                    sql.executeUpdate("update ... set xx=xx where x=x")
                }
        }
    }

    private withTx(Connection conn, Closure closure) {
        def savedAutoCommit = conn.autoCommit
        conn.autoCommit = false
        try {
            closure(conn)
            conn.commit()
        } catch (e) {
            conn.rollback()
            throw e
        } finally {
            conn.autoCommit = savedAutoCommit
        }
    }
}

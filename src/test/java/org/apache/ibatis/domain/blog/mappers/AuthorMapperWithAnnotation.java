package org.apache.ibatis.domain.blog.mappers;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.domain.blog.Author;
import org.apache.ibatis.jdbc.SQL;

/**
 * @author liuxiaocheng
 * @create 2020/3/28  9:43 上午
 */
public interface AuthorMapperWithAnnotation {

  // TODO: 2020/3/28 by wolf_love666 for study source code
//  @Select("select id, username, password, email, bio, favourite_section from author where id = #{id}")
//  Author selectAuthorInfoById(int id);

//  @SelectProvider(type =AuthorProvider.class,method = "selectAuthorWithPlaceholder")
//  Author selectAuthorWithPlaceholder(Author author);
//  @SelectProvider(type =AuthorProvider.class,method = "selectAuthorWithPlaceholderDollar")
//  Author selectAuthorWithPlaceholderDollar(Author author);
//  @SelectProvider(type =AuthorProvider.class,method = "selectAuthorWithPlaceholderWei")
//  Author selectAuthorWithPlaceholderWei(int id);
//  @SelectProvider(type =AuthorProvider.class,method = "selectAuthorWithPlaceholderParam")
//  Author selectAuthorWithPlaceholderParam(Author author);

//  @Insert("insert into author (id,username,password,email,bio) values (#{id},#{username},#{password},#{email},#{bio})")
//  int insertAuthor(Author author);
//
  @InsertProvider(type =AuthorProvider.class,method = "insertAuthorWithPlaceholder")
  int insertAuthorWithPlaceholder(Author author);
//  @InsertProvider(type =AuthorProvider.class,method = "insertAuthorWithPlaceholderWei")
//  int insertAuthorWithPlaceholderWei(Author author);

//  @Update("update author set username=#{username} where id =#{id}")
//  int updateAuthor(Author author);
//
//  @UpdateProvider(type =AuthorProvider.class,method = "updateAuthorWithPlaceholder")
//  void updateAuthorWithPlaceholder(Author author);
//
//  @Delete("delete from author where id = #{id}")
//  int deleteAuthor(Author author);
//
//  @DeleteProvider(type =AuthorProvider.class,method = "deleteAuthorWithPlaceholder")
//  int deleteAuthorWithPlaceholder(Author author);

   class AuthorProvider{
     public String selectAuthorWithPlaceholder(Author author){
       return new SQL(){{
         SELECT("id,username,password,email,bio").FROM("author").WHERE("id=#{id}");
       }}.toString();
     }
     public String selectAuthorWithPlaceholderWei(int id){
       return new SQL(){{
         SELECT("id,username,password,email,bio").FROM("author").WHERE("id like #id#");
       }}.toString();
     }
     public String selectAuthorWithPlaceholderDollar(Author author){
       return new SQL(){{
         SELECT("id,username,password,email,bio").FROM("author").WHERE("id=${id}");
       }}.toString();
     }
     public String selectAuthorWithPlaceholderParam(Author author){
       return new SQL(){{
         SELECT("id,username,password,email,bio").FROM("author").WHERE("id="+author.getId());
       }}.toString();
     }
     public String insertAuthorWithPlaceholderWei(Author author){
       return "insert into author (id,username,password,email,bio) values (#{id},"+author.getUsername()+",#{password},#{email},#{bio})";
     }
     public String insertAuthorWithPlaceholder(Author author){
       return new SQL(){{
         INSERT_INTO("Author").VALUES("id,username,password,email,bio","#{id},#{username},#{password},#{email},#{bio}");
       }}.toString();
     }
     public String updateAuthorWithPlaceholder(Author author){
       return new SQL(){{
         UPDATE("Author").SET("id=#{id}","username=#{username}","password=#{password}","email=#{email}","bio=#{bio}").WHERE("id=#{id}");
       }}.toString();
     }
     public String deleteAuthorWithPlaceholder(Author author){
       return new SQL(){{
         DELETE_FROM("Author").WHERE("id=#{id}");
       }}.toString();
     }
  }
}

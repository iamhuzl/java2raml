/*
 * Copyright [2018] [zhenglin Hu]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.herofrog.maven.plugin.example.web;

import cn.herofrog.maven.plugin.example.data.WebRequest;
import cn.herofrog.maven.plugin.example.data.WebResponse;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * springmvc-raml: TestResource
 *
 * @author huzhenglin (Employee ID: 17031596)
 * @version 1.0.0, 2018-11-01 22:52
 * @since 1.0.0, 2018-11-01 22:52
 */
@RestController
@RequestMapping("/api/v1/test")
public class TestResource {

    @RequestMapping("/get/{id}")
    public WebResponse get(WebRequest webRequest, @PathVariable("id") String id, @RequestParam String name,
                           @Validated @RequestBody String user,@RequestHeader Long userId){
        return null;
    }


    @RequestMapping(value = "/save/{id}",method = RequestMethod.POST)
    public WebResponse save(@Validated @RequestBody WebRequest webRequest, @PathVariable("id") String id){
        return null;
    }

}

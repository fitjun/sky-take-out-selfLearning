package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.utils.Myjwt;
import com.sky.vo.EmployeeLoginVO;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @PostMapping("/login")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = Myjwt.createToken(claims,jwtProperties.getAdminSecretKey(),jwtProperties.getUserTtl());
//        String token = JwtUtil.createJWT(
//                jwtProperties.getAdminSecretKey(),
//                jwtProperties.getAdminTtl(),
//                claims);
        log.info("生成的token：{}",token);
        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();
        return Result.success(employeeLoginVO);
    }

    /**
     * 退出
     *
     * @return
     */
    @PostMapping("/logout")
    public Result<String> logout() {
        return Result.success();
    }

    @PostMapping
    @ApiOperation("新增员工")
    public Result save(@RequestBody EmployeeDTO employeeDTO) {
        employeeService.save(employeeDTO);
        return Result.success();
    }

    @GetMapping("/page")
    @ApiOperation("员工分页查询")
    public Result<PageResult> pageQuery(EmployeePageQueryDTO employeePageQueryDTO) {
        PageResult pageResult = employeeService.pageQuery(employeePageQueryDTO);
        return Result.success(pageResult);
    }
    @PostMapping("/status/{status}")
    @ApiOperation("启用禁用员工账号")
    public Result ChangeEmpStatus(@PathVariable Integer status , @RequestParam Long id) {
        employeeService.updateEmpStatus(status,id);
        return Result.success();
    }

    @PutMapping
    @ApiOperation("修改员工信息")
    public Result alterEmp(@RequestBody EmployeeDTO employeeDTO){
        employeeService.updateEmp(employeeDTO);
        return Result.success();
    }
    @GetMapping("/{id}")
    @ApiOperation("根据id查询员工")
    public Result<Employee> findEmpById(@PathVariable Long id){
        Employee employee = employeeService.findById(id);
        return Result.success(employee);
    }
}

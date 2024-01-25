<#ftl strip_whitespace=true>
<#import '/spring.ftl' as spring />

<#macro input bindPath id='' name='' type='text' cssClass='form-control input-sm' extra=''>
    <@spring.bind path=bindPath />
    <input type="${type}"
           id="${id???then(spring.status.expression, id)}"
           name="${name???then(spring.status.expression, name)}"
           value="${spring.status.value!''}"
           class="${cssClass}"${extra???then((' ' + extra)?html, '')} />
</#macro>

<#-- with bootstrap -->
<#-- 에러가 존재할 경우 각 에러의 첫 번째 항목만 출력 -->
<#macro bootstrapInput bindPath id='' name='' type='text' formClass='form-control input-sm' divClass='col-md-3' showError=true extra=''>
    <@spring.bind path=bindPath />
    <#if showError>
        <#assign hasError=false />
        <#assign errorMessage='' />
        <#if spring.status.errorMessages?? && spring.status.errorMessages?has_content>
            <#assign  hasError=true />
            <#assign errorMessage=spring.status.errorMessages[0] />
        </#if>
        <div class="${divClass}${hasError?then(' has-error', '')}">
            <@input bindPath=bindPath id=id name=name type=type cssClass=formClass extra=extra />
            <#if hasError && errorMessage??>
                <span class="help-block">${errorMessage}</span>
            </#if>
        </div>
    <#else>
        <div class="${divClass}">
            <@input bindPath=bindPath id=id name=name type=type cssClass=formClass extra=extra />
        </div>
    </#if>
</#macro>
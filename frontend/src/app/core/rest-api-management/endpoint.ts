export const API_ENDPOINTS = {
  USER: {
    UPDATE: '/user/update',
    PROFILE: '/user/profile',
    CHANGEPASSWORD: '/user/changePassword',
    DELETE: '/user/delete',
    ISADMIN: '/user/isAdmin',
    ANNOTATIONS: '/user/profile/annotation'
  },
  AUTH: {
    LOGIN: '/auth/login',
    REGISTER: '/auth/register',
    LOGOUT: '/auth/logout',
    STATUS: '/auth/status',
  },
  ADMIN: {
    USERS: '/admin/users',
    BAN: '/admin/users/ban',
    UNBAN:'/admin/users/unban/',
    CHANGEROLE:'/admin/users/changeRole',
    MAPS: {
      FINALMAPS:{
        DELETE: '/admin/finalMaps/delete',
        UPLOAD: '/admin/finalMaps/uploadShapeFile'
      },
      RASTERMAPS: {
        UPLOAD: '/admin/rasterMaps/file'
      }
    },
    EVALUATIONFORM:{
      GETALLFORMFORAMAP:"/admin/evaluationForm/allFormForFinalMap",
      DELETEFORM:"/admin/evaluationForm/deleteForm"
    }
  },
  ANNOTATIONS:{
    GET : "/annotation",
    POST : "/annotation/save"
  },
  FINALMAPS: {
    ALLMAPS: '/finalMaps/allMaps',
    GET: '/finalMaps'
  },
  RASTERMAPS: {
    RASTERS: '/rasterMaps/rasters',
    RISKFACTORS: '/rasterMaps/riskFactors'
  },
  EVALUATIONFORM:{
    SAVE:"/evaluationForm/saveForm",
    UPDATE:"/evaluationForm/updateForm",
    GETMYFORMFORADIVFORAMAP:"/evaluationForm/myForm",
    GETALLFORMFORAMAP:"/evaluationForm/allFormForFinalMap",
    DELETEFORM:"/evaluationForm/deleteForm"
  },
  MEASURE: {
    EVAlUATORAGREEMENT: {
      DIVISIONALCONSENSUSSCORE:"/evaluatorAgreementMeasure/divisionalConsensusScore",
      NATIONALCONSENSUSSCORE:"/evaluatorAgreementMeasure/nationalConsensusScore",
      KRIPPENDORFFALPHA:"/evaluatorAgreementMeasure/krippendorffAlpha"
    },
    MEAN: {
      DIVISIONALAGREEMENTSCORE:"/meanMeasure/meanDivisionalAgreementScore",
      CERTAINTY: "/meanMeasure/meanCertainty",
      DOMINANTPERCEIVEDRISKLEVEL:"/meanMeasure/dominantPerceivedRiskLevel"
    },
    MODELEVALUATION:{
      WEIGHTEDDIVISIONALLEVELAGREEMENTSCORE:"/modelEvaluationMeasure/weightedDivisionalLevelAgreementScore",
      NATIONALMODELFIELDAGREEMENTSCORE:"/modelEvaluationMeasure/nationalModelFieldAgreementScore"
    }
  },
  EVALUATORPROFILE: {
    SAVE: "/evaluatorProfile/save",
    HASPROFILE: "/evaluatorProfile/hasProfile",
    GETPROFILE: "/evaluatorProfile/getProfile",
    UPDATE: "/evaluatorProfile/update"
  },
  REPORT: {
    GET:"/report/getReport"
  }
} as const;
